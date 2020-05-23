(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [apollo.midi :refer [play]]
            [apollo.utils :refer [sanitize]]
            [apollo.parser :refer [split-meta-score
                                   get-instrument-name
                                   convert-score-to-midi]]))

(defn get-instrument-list []
  (loop [mapping {}
         iteration 0
         instruments (str/split (slurp "instruments.txt") #"\n")]
  (if (empty? instruments)
    mapping
    (recur (into mapping (hash-map (sanitize (first instruments)) iteration))
           (inc iteration)
           (rest instruments)))))

(defn get-instrument-numbers [instruments instrument-names]
  "Gets the instrument numbers using the instrument names. Uses a dictionary
  lookup in order to find the relevant map from name to number"
  (vec (map #(get instruments %) instrument-names)))

(def cli-options
  [[nil "--roll" "The notes to play"
   :required "Path to piano roll"]])

(defn -main [& args]
  (let [roll (:roll (:options (parse-opts args cli-options)))]
    (let [[meta score] (split-meta-score roll)
          instruments (get-instrument-list)]
      (play
        (get-instrument-numbers instruments (vector (sanitize (get-instrument-name meta))))
        (convert-score-to-midi meta score)))))