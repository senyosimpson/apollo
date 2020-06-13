(ns apollo.parser
  (:require [clojure.string :as str]
            [apollo.utils :refer [sanitize]]))


(defn get-instruments []
  "
  Gets a map of all the available instruments with their corrresponding
  instrument numbers
  
  {acoustic-grand-piano 0 bright-acoustic-piano 1 ...}
  "
  (loop [mapping {}
         iteration 0
         instruments (str/split (slurp "instruments.txt") #"\n")]
  (if (empty? instruments)
    mapping
    (recur (into mapping (hash-map (sanitize (first instruments)) iteration))
           (inc iteration)
           (rest instruments)))))


(def instruments (get-instruments))


(defn get-instrument-number [instrument]
  "
  Gets the relevant instrument number
  
  Arguments:
    instrument - the name of the instrument
  "
  (get instruments (sanitize instrument)))


(defn get-instrument-name [score-metadata]
  "
  Get the instrument name from the score metadata
  
  Arguments:
    score-metadata - the score metadata
  "
  (subs score-metadata 0 (str/index-of score-metadata ":")))


(defn get-note-offsets [notes]
  "
  Gets the offsets of a note from a base note. i.e D has
  an offset of 2 relative to C. Returns a mapping from note
  to offset e.g {C 0, C# 1, D 2, D# 3}
  
  Arguments:
    notes - a list/vector of the names of the notes (i.e [c d e f])
  "
  (loop [mapping {}
         iteration 0
         notes notes]
    (if (empty? notes)
      mapping
      (recur (into mapping (hash-map (first notes) iteration))
             (inc iteration)
             (rest notes)))))


(def note-offsets (get-note-offsets ["c" "c#" "d" "d#" "e" "f" "f#" "g" "g#" "a" "a#" "b"]))


(defn get-score-metadata [score]
  "
  Get the score metadata from the score. This is usually
  the instrument name and octave
  
  Arguments:
    score - the score in its string representation
  "
  (first (str/split score #"\n")))


(defn get-notes [score]
  "
  Get the notes from the score
  
  Arguments:
    score - the score in its string representation
  "
  (last (str/split score #"\n")))


(defn get-octave-base-note
  "
  Gets the first midi note number for an octave
  
  Arguments:
    octave - the relevant octave (usually a number between
      1 - 7)
  "
  ([octave]
    (+ 24 (* 12 (- octave 1))))
  ([]
    (get-octave-base-note 4)))


(defn get-score-octave [score]
  "
  Get the octave of the musical score
  
  Arguments:
    score - the score in its string representation
  "
  (let [octave (subs score (str/index-of score ":"))]
    (if (= ":" octave)
        4
        (Integer/parseInt (str (last octave))))))


(defn to-midi-note [note octave]
  "
  Converts a string representation of a note to a midi number.
  For example, middle c -> 60

  Arguments:
    octave - the octaves the note is in (e.g 4)
    note - the note in string representation to convert
  "
  (+ (get-octave-base-note octave) (get note-offsets note)))


(defn get-note-letter [note]
  "
  Gets the letter of the note. This is necessary as notes can be specified with a duration.
  An example note is c4 which is the note c for 4 quarter notes. This function returns the letter of
  this note i.e c
  "
  (str (first note)))


(defn get-note-duration
  "
  Gets the duration of a note. This is specified on the note
  string representation. For example, c4 has a duration of 4
  quarter notes. If no duration is attached to the note, either
  a default duration of 1 tick is returned otherwise nil
  
  Arguments:
    note - the note in its string representation
    use-default - if set, returns a default of 1 otherwise returns nil
  "
  ([note use-default]
    (let [note-letter (get-note-letter note)
          duration (str (last note))]
      (if (= note-letter duration)
        (if use-default 1 nil)
        (Integer/parseInt duration))))
  ([note]
    (get-note-duration note false)))


(defn get-note-letter [note]
  "
  Gets the letter of the note. This is necessary as notes can
  be specified with a duration. An example note is c4 which is
  the note c for 4 quarter notes. This function returns the letter
  of this note i.e c

  Arguments:
    note: the note in its string representation
  "
  (str (first note)))


; this needs a name change to something more descriptive
(defn get-valid-duration [global-duration note-duration]
  "
  This is a utility function for building the apollo representation
  of the score. If a note has no duration specified, it will default
  to the last specified duration. i.e if we have notes [c4, d, e, f2, g],
  d and e should have a duration of 4 while g will have a duration of 2.
  This function returns the relevant duration depending on whether the
  note duration is set.
  
  Arguments:
    global-duration - the last specified duration
    note-duration - the duration of the current note
  "
  (if (nil? note-duration)
    global-duration
    note-duration))


(defn to-apl-notes [notes octave channel]
  "
  Converts notes to the apollo note representation. This is the core structure
  that defines the data needed for a note to be played by the sound engine. It is a 
  hash map with the data:
    {
      :note c
      :midi-note 60
      :volume 60
      :channel 0
      :duration 2
    }

  Arguments:
    notes - a list of notes
    octave - the octave the notes are in (i.e 4)
    channel - the midi channel
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
                                :midi-note (to-midi-note note octave)
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
      :instrument Acoustic Grand Piano
      :instrument-number 0
      :octave 4
      :notes <apl-notes>
    }

  Arguments:
    score - the musical score and score information (in its string representation)
    channel - the midi channel
  "
  (let [score-info (get-score-info score)
        instrument (get-instrument-name score-info)
        octave (get-score-octave score-info)
        notes (str/split (get-score-notes score) #" ")]
    {:instrument instrument
     :instrument-number (get-instrument-number instrument)
     :octave octave
     :channel channel
     :notes (to-apl-notes notes octave channel)}))


(defn apl-score-from-file [file]
  "
  Reads a file and builds the internal apollo representation of a score. Since
  there may be more than one musical instrument, we name each instrument
  and its notes a subscore.

  Arguments:
    file - the file path to the .apl file
  "
  (let [subscores (str/split (slurp file) #"\n\n")
        channels (range (count subscores))]
    (for [[subscore channel] (partition 2 (interleave subscores channels))]
      (to-apl-score subscore channel))))