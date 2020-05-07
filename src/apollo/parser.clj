(ns apollo.parser
  (:require [clojure.string :as str]
            [apollo.midi :refer [get-octave-base-note
                                 note-to-midi
                                 note-offsets]]))

(def note-offsets-map (note-offsets ["c" "c#" "d" "d#" "e" "f"
                                     "f#" "g" "g#" "a" "a#" "b"]))

(defn split-meta-score [apl]
  (str/split (slurp apl) #"\n"))

(defn get-instrument-name [meta]
  (subs meta 0 (str/index-of meta ":")))

(defn get-score-octave [meta]
  (let [octave (subs meta (str/index-of meta ":"))]
    (if (= ":" octave)
      (get-octave-base-note)
      (get-octave-base-note (Integer/parseInt (str (last octave)))))))

(defn convert-score-to-midi [meta score]
  (map (partial note-to-midi (get-score-octave meta) note-offsets-map)
       (str/split score #" ")))
         
