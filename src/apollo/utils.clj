(ns apollo.utils
  (:require [clojure.string :as str]))


(defn basename [name]
  "
  Gets the basename of an instrument name
  e.g Bass 4 (Lead + Pad) -> bass4
  
  Arguments:
    name - the instrument name"
  (let [index (str/index-of name "(")]
    (if (nil? index)
      name
      (str/trim (subs name 0 index)))))


(defn sanitize [name]
  "
  Sanitize the name of the instrument. This performs the
  following transformation, Acoustic Grand Piano -> acoustic-grand-piano

  Arguments:
    name - the instrument name
  "
  ((comp
    #(.toLowerCase %)
    #(str/replace % #" " "-")
    basename)
   string))
