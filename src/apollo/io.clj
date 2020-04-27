(ns apollo.io
  (:require [clojure.string :as str]))

(defn read-piano-roll [filename]
  "Reads piano roll file"
  (map #(Integer/parseInt %) (str/split (slurp filename) #" ")))