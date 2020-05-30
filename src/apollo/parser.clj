(ns apollo.parser
  (:require [clojure.string :as str]
            [apollo.utils :refer [sanitize]]))


(defn get-instrument-list []
  (loop [mapping {}
         iteration 0
         instruments (str/split (slurp "instruments.txt") #"\n")]
  (if (empty? instruments)
    mapping
    (recur (into mapping (hash-map (sanitize (first instruments)) iteration))
           (inc iteration)
           (rest instruments)))))


(def instrument-names (get-instrument-list))


(defn get-instrument-number [instrument]
  (get instrument-names (sanitize instrument)))


(defn get-instrument-name [meta]
  (subs meta 0 (str/index-of meta ":")))


(defn note-offsets [notes]
  "Gets the offsets of a note from a base note. i.e D has an offset of 2 relative to C.
  Returns a mapping from note to offset e.g {C 0, C# 1, D 2, D# 3}"
  (loop [mapping {}
         iteration 0
         notes notes]
    (if (empty? notes)
      mapping
      (recur (into mapping (hash-map (first notes) iteration))
             (inc iteration)
             (rest notes)))))


(def note-offsets-map (note-offsets ["c" "c#" "d" "d#" "e" "f" "f#" "g" "g#" "a" "a#" "b"]))


(defn get-score-info [score]
  "Get the high level score information from the score"
  (first (str/split score #"\n")))


(defn get-notes [score]
  "Get the notes from the score"
  (last (str/split score #"\n")))


(defn get-octave-base-note
  "Gets the first midi note number for an octave"
  ([octave]
    (+ 24 (* 12 (- octave 1))))
  ([]
    (get-octave-base-note 4)))


(defn get-score-octave [score]
  "Get the octave of the musical score"
  (let [octave (subs score (str/index-of score ":"))]
    (if (= ":" octave)
        4
        (Integer/parseInt (str (last octave))))))


(defn to-midi [note octave]
  "
  Converts a string representation of a note to a midi number.
  For example, middle c -> 60

  Arguments:
    octave - the octaves the note is in (e.g 4)
    note - the note in string representation to convert
  "
  (+ (get-octave-base-note octave) (get note-offsets-map note)))


(defn get-note-duration
  "
  Gets the duration of a note. This is specified on the note string representation. 
  For example, c4 has a duration of 4 quarter notes. If no duration is attached to the note,
  either a default duration of 1 tick is returned otherwise nil"
  ([note use-default]
    (let [note (str (first note))
          duration (str (last note))]
      (if (= note duration)
        (if use-default 1 nil)
        (Integer/parseInt duration))))
  ([note]
    (get-note-duration note false)))


(defn get-note-letter [note]
  "
  Gets the letter of the note. This is necessary as notes can be specified with a duration.
  An example note is c4 which is the note c for 4 quarter notes. This function returns the letter of
  this note i.e c
  "
  (str (first note)))


; this needs a name change to something more descriptive
(defn get-valid-duration [global-duration note-duration]
  (if (nil? note-duration)
    global-duration
    note-duration))


(defn to-apl-notes [notes octave channel]
  "
  Converts notes to the apollo note representation. This is the core structure
  that defines the data needed for a note to be played by the sound engine. It is a 
  hash map with the data:
    {
      :note \"c\"
      :midi-note 60
      :volume 60
      :channel 0
      :duration 2
    }

  Arguments:
    notes - a list of notes
    octave - the octave the notes are in (i.e 4)
  "
  (loop [apl-notes []
         notes notes
         offset 4
         global-duration (get-note-duration (first notes) true)]
    (if (empty? notes)
      apl-notes
      (let [note (get-note-letter (first notes))
            duration (get-valid-duration global-duration (get-note-duration notes))]
        (recur (conj apl-notes {:note note
                                :midi-note (to-midi note octave)
                                :volume 60
                                :offset offset
                                :channel channel
                                :duration duration})
                (rest notes)
                (+ offset duration)
                duration)))))


(defn to-apl-score [score channel]
  "
  Builds the internal apollo representation of a score. This is the main structure used
  by the audio engine. This is a hash map with the data:
    {
      :instrument acoustic grand piano
      :instrument-number 0
      :octave 4
      :notes <apl-notes>
    }

  Arguments:
    score - the musical score and score information (in its string representation)
  "
  (let [score-info (get-score-info score)
        instrument (get-instrument-name score-info)
        octave (get-score-octave score-info)
        notes (str/split (get-notes score) #" ")]
    {:instrument instrument
     :instrument-number (get-instrument-number instrument)
     :octave octave
     :channel channel
     :notes (to-apl-notes notes octave channel)}))


(defn apl-scores-from-file [file]
  "
  Reads a file and builds the internal apollo representation of a score.

  Arguments:
    file - the file path to the .apl file
  "
  (let [scores (str/split (slurp file) #"\n\n")
        channels (range (count scores))]
    (for [[score channel] (partition 2 (interleave scores channels))]
      (to-apl-score score channel))))