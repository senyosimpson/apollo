(ns apollo.audio
  (:import [javax.sound.midi MidiSystem]))

(defn get-synth
  "Gets a synth player"
  [channelNum instrumentNum]
  (let [synth (doto (MidiSystem/getSynthesizer) .open)]
    (do
      (let [channel (aget (.getChannels synth) channelNum)
            instrument (aget (.getAvailableInstruments synth) instrumentNum)]
        (println (str "Instrument is a " (.getName instrument)))
        (.loadInstrument synth instrument)
        (.programChange channel instrumentNum)
        (fn [volume note duration]
          (do
            (.noteOn channel note volume)
            (Thread/sleep duration)
            (.noteOff channel note)))))))