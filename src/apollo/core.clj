(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [apollo.audio :refer [play]]
            [apollo.io :refer [read-piano-roll]]))

(defn strToInt [val]
  "Parses a string into an integer"
  (Integer/parseInt val))

(def cli-options
  [[nil "--channel" "The channel number"
    :required "The channel to play on"
    :default 0
    :parse-fn strToInt]
  [nil "--instrument" "The number of the instrument to play"
   :required "The instrument to play"
   :default 0
   :parse-fn strToInt]
  [nil "--roll" "The notes to play"
   :required "Path to piano roll"]])

(defn -main []
  (play [25 60] "midifiles/c-major.mid"))