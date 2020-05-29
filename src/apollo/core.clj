(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [apollo.midi :refer [play]]
            [apollo.utils :refer [sanitize]]
            [apollo.parser :refer [apl-scores-from-file]]))

(def cli-options
  [[nil "--roll" "The notes to play"
   :required "Path to piano roll"]])

(defn -main [& args]
  (let [roll (:roll (:options (parse-opts args cli-options)))]
    (play (apl-scores-from-file roll))))
