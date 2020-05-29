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


(defn get-note-data [notes octave]
  "
  Creates a vector of notes with their corresponding data. This is the core structure
  that defines the data needed for a note to be played by the sound engine. It is a 
  hash map with the data:
    {
      :note \"c\"
      :midi-note 60
      :volume 60
      :channel 0
      :duration 300
    }

  Arguments:
    notes - a list of notes
    octave - the octave the notes are in (i.e 4)
  "
  (loop [note-data []
         notes notes]
    (if (empty? notes)
      note-data
      (recur (conj note-data {:note (first notes)
                              :midi-note (to-midi (first notes) octave)
                              :volume 60
                              :channel 0
                              :duration 2})
             (rest notes)))))


(defn build-score-repr [score]
  "
  Builds the internal representation of a score. This is the main structure used
  by the audio engine. This is a hash map with the data:
    {
      :instrument acoustic grand piano
      :instrument-no 0
      :octave 4
      :notes (note-repr)
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
     :notes (get-note-data notes octave)}))

(defn score-repr-from-file [apl]
  "
  Reads a file and builds the internal representation of a score.

  Arguments:
    apl - the file path to the .apl file
  "
  (for [score (str/split (slurp apl) #"\n\n")] (build-score-repr score)))