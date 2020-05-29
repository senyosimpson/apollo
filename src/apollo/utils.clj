(ns apollo.utils
  (:require [clojure.string :as str]))


(defn basename [string]
  "Gets the basename of an instrument name
   e.g Bass 4 (Lead + Pad) -> bass4"
  (let [index (str/index-of string "(")]
    (if (nil? index)
      string
      (str/trim (subs string 0 index)))))


(defn sanitize [string]
  "Sanitize the name of the instrument"
  ((comp
    #(.toLowerCase %)
    #(str/replace % #" " "-")
    basename)
   string))
