(ns apollo.midi
  (:import (javax.sound.midi ShortMessage MidiEvent MidiSystem Sequence)))


(defn create-midi-event [message tick]
  (new MidiEvent message tick))


(defn create-program-change-event [channel instrument-num]
  "Creates a midi note on message"
  (create-midi-event 
    (doto (new ShortMessage) (.setMessage ShortMessage/PROGRAM_CHANGE channel instrument-num channel))
    0))


(defn create-note-on-event [note channel volume tick]
  "Creates a midi note on message"
  (create-midi-event 
    (doto (new ShortMessage) (.setMessage ShortMessage/NOTE_ON channel note volume))
    tick))


(defn create-note-off-event [note channel volume tick]
  "Creates a midi note off message"
  (create-midi-event
    (doto (new ShortMessage) (.setMessage ShortMessage/NOTE_OFF channel note volume))
    tick))


(defn add-program-change [track channel instrument-num]
  (.add track (create-program-change-event channel instrument-num)))


(defn add-note [track note channel volume position duration]
  (do
    (.add track (create-note-on-event note channel volume position))
    (.add track (create-note-off-event note channel volume (+ position duration)))))


(defn populate-track [track apl-score]
  "
  Populates a track with an instruments notes on the chosen channel
  "
  (let [channel (:channel apl-score)
        instrument-num (:instrument-number apl-score)
        notes (:notes apl-score)]
    (add-program-change track channel instrument-num)
    (doseq [[note tick] (map list notes (take (count notes) (iterate (partial + 2) 4)))]
      (add-note track (:midi-note note) channel (:volume note) (:offset note) (:duration note)))))


(defn set-sequence [sequencer apl-scores]
  (let [sequence (new Sequence Sequence/PPQ 1)]
    (let [track (.createTrack sequence)]
      (doseq [apl-score apl-scores] (populate-track track apl-score))
    (doto sequencer (.setSequence sequence)))
    sequencer))


(defn get-sequencer []
  (let [synth (doto (MidiSystem/getSynthesizer) .open)
        sequencer (doto (MidiSystem/getSequencer) .open)]
    (let [seq-transmitter (.getTransmitter sequencer)
          synth-receiver (.getReceiver synth)]
      (.setReceiver seq-transmitter synth-receiver))
    sequencer))


(defn play [apl-scores]
  (let [sequencer (get-sequencer)]
    (set-sequence sequencer apl-scores)
    (.start sequencer)
    (while (true? (.isRunning sequencer)))
    (.close sequencer)))
