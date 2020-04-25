(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [apollo.audio :refer [get-synth]]))

(defn strToInt
  "Parses a string into an integer"
  [val]
  (Integer/parseInt val))

(def cli-options
  [[nil "--channel" "The channel number"
    :default 0
    :parse-fn strToInt]
  [nil "--instrument" "The number of the instrument to play"
   :default 0
   :parse-fn strToInt]])
  
(defn -main [& args]
  (let [{:keys [channel instrument]} (:options (parse-opts args cli-options))]
    ((get-synth channel instrument) 60 60 250)))