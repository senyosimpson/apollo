(ns apollo.audio
  (:import [javax.sound.midi MidiSystem])
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def instrument-list
  (str/split (slurp "instruments.txt") #"\n"))

(defn set-instrument-channel [synth instrumentNum channelNum]
  "Sets a channel to the specified instrument"
  (let [channel (aget (.getChannels synth) channelNum)
        instrument (aget (.getAvailableInstruments synth) instrumentNum)]
    (println (str "Setting instrument " (.getName instrument) " on channel " channelNum))
    (.loadInstrument synth instrument)
    (.programChange channel instrumentNum)))

(defn set-sequence [sequencer midifile]
  (let [sequence (MidiSystem/getSequence (io/file midifile))]
    (doto sequencer (.setSequence sequence))))

(defn get-sequencer [instrumentNums]
  (let [synth (doto (MidiSystem/getSynthesizer) .open)
        sequencer (doto (MidiSystem/getSequencer) .open)]
    (doseq [[instrumentNum channelNum]
            (map list instrumentNums (range (count instrumentNums)))]
      (set-instrument-channel synth instrumentNum channelNum))
    (let [sequencerTransmitter (.getTransmitter sequencer)
          synthesizerReceiver (.getReceiver synth)]
      (.setReceiver sequencerTransmitter synthesizerReceiver))
    sequencer))

(defn play [instrumentNums midifile]
  (let [sequencer (get-sequencer instrumentNums)]
    (set-sequence sequencer midifile)
    (.start sequencer)
    (while (true? (.isRunning sequencer)))
    (.close sequencer)))
