(ns apollo.utils
  (:require [clojure.string :as str]))


(defn sanitize [name]
  "
  Sanitize the name of the instrument. This performs the
  following transformation, Acoustic Grand Piano -> acoustic-grand-piano

  Arguments:
    name - the instrument name
  "
  ((comp
    #(.toLowerCase %)
    #(str/replace % #"\(" "")
    #(str/replace % #"\)" "")
    #(str/replace % #" " "-"))
   name))


(defn is-digit [string]
  (not (nil? (re-matches #"\d" string))))