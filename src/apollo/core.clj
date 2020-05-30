(ns apollo.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [apollo.midi :refer [play]]
            [apollo.utils :refer [sanitize]]
            [apollo.parser :refer [apl-score-from-file]]))

(def cli-options
  [[nil "--score" "The musical score"
   :required "Path to the musical score to play"]])

(defn -main [& args]
  (let [score (:score (:options (parse-opts args cli-options)))]
    (play (apl-score-from-file score))))
