(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [apollo.audio :refer [play]]
            [apollo.utils :refer [sanitize]]))

(def cli-options
  [[nil "--roll" "The notes to play"
   :required "Path to piano roll"]])

(defn get-instrument-list []
  (loop [mapping {}
         iteration 0
         instruments (str/split (slurp "instruments.txt") #"\n")]
  (if (empty? instruments)
    mapping
    (recur (into mapping (hash-map (keyword (sanitize (get instruments 0))) iteration))
           (inc iteration)
           (vec (rest instruments))))))

(defn get-instrument-numbers [instruments instrument-names]
  (vec (map #((keyword %) instruments) instrument-names)))

(defn -main []
  (let [instruments (get-instrument-list)]
    (play
      (get-instrument-numbers instruments ["violin"])
      "midifiles/c-major.mid")))