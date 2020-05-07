(ns apollo.midi)

(defn note-offsets [notes] 
  (loop [mapping {}
         iteration 0
         notes notes]
    (if (empty? notes)
      mapping
      (recur (into mapping (hash-map (first notes) iteration))
             (inc iteration)
             (rest notes)))))

(defn note-to-midi [base note-offsets-map note]
  "Converts a note to a midi number. For example, middle c -> 60"
  (+ base (get note-offsets-map note)))

(defn get-octave-base-note
  "Gets the first midi note number for an octave"
  ([octave]
    (+ 24 (* 12 (- octave 1))))
  ([]
    (get-octave-base-note 4)))
