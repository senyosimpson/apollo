(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [apollo.audio :refer [get-synth play-notes play]]
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
  
; (defn -main [& args]
;   (let [{:keys [channel instrument roll]} (:options (parse-opts args cli-options))]
;     (let [notes (read-piano-roll roll)]
;       (play-notes (get-synth channel instrument) (concat notes (reverse notes))))))

(defn -main []
  (play [0 60] "midifiles/c-major.mid"))