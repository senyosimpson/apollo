
(ns apollo.utils-test
  (:require [clojure.test :refer :all]
            [apollo.utils :refer :all]))


(deftest basename-test
    (is (= (sanitize "Acoustic Grand Piano") "acoustic-grand-piano"))
    (is (= (sanitize "Electric Bass (finger)") "electric-bass-finger"))
    (is (= (sanitize "Lead 1 (square)") "lead-1-square")))