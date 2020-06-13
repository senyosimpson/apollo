(ns apollo.midi
  (:import (javax.sound.midi ShortMessage MidiEvent MidiSystem Sequence)))


(defn create-midi-event [message tick]
  "
  Creates a midi event
  
  Arguments:
    message - the midi message that contains midi information
    tick - the position of the midi event
  "
  (new MidiEvent message tick))


(defn create-program-change-event [channel instrument-num]
  "
  Creates a midi note on message
  
  Arguments:
    channel - the number of the midi channel
    instrument-num - the instrument number (i.e instrument number 0
      is an acoustic grand piano)
  "
  (create-midi-event 
    (doto (new ShortMessage) (.setMessage ShortMessage/PROGRAM_CHANGE channel instrument-num channel))
    0))


(defn create-note-on-event [note channel volume tick]
  "
  Creates a midi note on message
  
  Arguments:
    note - the midi note number
    channel - the number of the midi channel
    volume - the volume of note
    tick - the position of the midi event
  "
  (create-midi-event 
    (doto (new ShortMessage) (.setMessage ShortMessage/NOTE_ON channel note volume))
    tick))


(defn create-note-off-event [note channel volume tick]
  "
  Creates a midi note off message
  
  Arguments:
    note - the midi note number
    channel - the number of the midi channel
    volume - the volume of note
    tick - the position of the midi event
    "
  (create-midi-event
    (doto (new ShortMessage) (.setMessage ShortMessage/NOTE_OFF channel note volume))
    tick))


(defn add-program-change [track channel instrument-num]
  "
  Add a program change event to the track

  Arguments:
    track - the track to add the message to
    channel - the number of the midi channel
    instrument-num - the number of the instrument (i.e instrument number 0 is
      an acoustic grand piano)
  "
  (.add track (create-program-change-event channel instrument-num)))


(defn add-note [track note channel volume tick duration]
  "
  Adds a note event to the track
  
  Arguments:
    track - the track to add the midi note message to
    note - the midi note number
    channel - the number of the midi channel
    volume - the volume of note
    tick - the position of the note
    duration - the length of the note specified in ticks
  "
  (.add track (create-note-on-event note channel volume tick))
  (.add track (create-note-off-event note channel volume (+ tick duration))))


(defn populate-track [track apl-score]
  "
  Populates a track with an instruments notes on the chosen channel

  Arguments:
    track - the track to populate with score
    apl-score - the score in the apollo (apl) representation
  "
  (let [channel (:channel apl-score)
        instrument-num (:instrument-number apl-score)
        notes (:notes apl-score)]
    (add-program-change track channel instrument-num)
    (doseq [[note tick] (map list notes (take (count notes) (iterate (partial + 2) 4)))]
      (add-note track (:midi-note note) channel (:volume note) (:offset note) (:duration note)))))


(defn set-sequence [sequencer apl-score]
  "
  Creates a sequence with the note information stored in the apollo score. The
  sequence is added to the sequencer for playing
  
  Arguments:
    sequencer - a java midi sequencer object that is used to play the score
    apl-score - the apollo score to add to the sequencer
  "
  (let [sequence (new Sequence Sequence/PPQ 1)]
    (let [track (.createTrack sequence)]
      (doseq [apl-subscore apl-score] (populate-track track apl-subscore))
    (doto sequencer (.setSequence sequence)))
    sequencer))


(defn get-sequencer []
  "
  Gets a java midi sequencer object
  "
  (let [synth (doto (MidiSystem/getSynthesizer) .open)
        sequencer (doto (MidiSystem/getSequencer) .open)]
    (let [seq-transmitter (.getTransmitter sequencer)
          synth-receiver (.getReceiver synth)]
      (.setReceiver seq-transmitter synth-receiver))
    sequencer))


(defn play [apl-score]
  "
  The function for playing an apollo score
  
  Arguments:
    apl-score - the apollo score to play
  "
  (let [sequencer (get-sequencer)]
    (set-sequence sequencer apl-score)
    (.start sequencer)
    (while (true? (.isRunning sequencer)))
    (.close sequencer)))
