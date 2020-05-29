# Development Diary

This project is the first project I've done in Clojure. At this point, I'm fairly comfortable with Clojure and the syntax, though I still have much to learn! This diary serves as a means to formalise the development process from here on out. The additional benefit is that you get to read about the challenges faced, how they were overcome and the things I've learned on the way.

## 1000 Foot View - 23/05/2020

As stated in the readme, this is a musical programming language inspired by Alda. The reason I chose this as my first project is really just because I am an avid music consumer and thought it would be an interesting project tackle - I was right! I know absolutely no music theory and do not have any understanding of midi either so there's plenty for me to sink my teeth into. Over time, I expect this project to grow into something beautiful but for now, it is more a means to learn more about the Clojure language. Therefore, it won't be as feature-complete, stable and efficient as Alda. Once again, as I am learning, I am refraining from reading Alda source code as much as possible. When learning, I prefer to do things the hard way. However, I do take inspiration from the "spirit" of Alda - how it goes about parsing files, building midi files and playing them.

At present, Apollo can take a (.apl) file, parse it, build the midi track and play the notes. This is limited to only one instrument and a set of notes - nothing complex can be built. The next step is to be able to play multiple instruments with different scores and improve the structure of the music (think timing of midi notes).

## Playing multiple instruments - 23/05/2020

In order to facilitate this, I take inspiration from Alda. It converts an input file into a map of score information which is used by the audio engine to build the song. Currently in Apollo, no intermediate representation is built. This is suboptimal and so I will move towards the same structure as Alda. An example of this can be seen in Alda's [development guide](https://github.com/alda-lang/alda-core/blob/master/doc/development-guide.md).

### Building tracks with multiple instruments - 29/05/2020

At the moment I can parse multiple instruments and build an intermediate representation as shown below. Some things are hardcoded and will be changed when I am more comfortable with how the system is put together.

```clojure
({:instrument "Acoustic Grand Piano",
  :instrument-number 0,
  :octave 4,
  :notes
  [{:note "c", :midi-note 60, :volume 60, :channel 0, :duration 2}
   {:note "d", :midi-note 62, :volume 60, :channel 0, :duration 2}
   {:note "e", :midi-note 64, :volume 60, :channel 0, :duration 2}
   {:note "f", :midi-note 65, :volume 60, :channel 0, :duration 2}
   {:note "g", :midi-note 67, :volume 60, :channel 0, :duration 2}
   {:note "a", :midi-note 69, :volume 60, :channel 0, :duration 2}
   {:note "b", :midi-note 71, :volume 60, :channel 0, :duration 2}
   {:note "c", :midi-note 60, :volume 60, :channel 0, :duration 2}
   {:note "c", :midi-note 60, :volume 60, :channel 0, :duration 2}
   {:note "b", :midi-note 71, :volume 60, :channel 0, :duration 2}
   {:note "a", :midi-note 69, :volume 60, :channel 0, :duration 2}
   {:note "g", :midi-note 67, :volume 60, :channel 0, :duration 2}
   {:note "f", :midi-note 65, :volume 60, :channel 0, :duration 2}
   {:note "e", :midi-note 64, :volume 60, :channel 0, :duration 2}
   {:note "d", :midi-note 62, :volume 60, :channel 0, :duration 2}
   {:note "c", :midi-note 60, :volume 60, :channel 0, :duration 2}]}
 {:instrument "Alto Sax",
  :instrument-number 65,
  :octave 4,
  :notes
  [{:note "c", :midi-note 60, :volume 60, :channel 1, :duration 2}
   {:note "d", :midi-note 62, :volume 60, :channel 1, :duration 2}
   {:note "e", :midi-note 64, :volume 60, :channel 1, :duration 2}
   {:note "f", :midi-note 65, :volume 60, :channel 1, :duration 2}
   {:note "g", :midi-note 67, :volume 60, :channel 1, :duration 2}
   {:note "a", :midi-note 69, :volume 60, :channel 1, :duration 2}
   {:note "b", :midi-note 71, :volume 60, :channel 1, :duration 2}
   {:note "c", :midi-note 60, :volume 60, :channel 1, :duration 2}
   {:note "c", :midi-note 60, :volume 60, :channel 1, :duration 2}
   {:note "b", :midi-note 71, :volume 60, :channel 1, :duration 2}
   {:note "a", :midi-note 69, :volume 60, :channel 1, :duration 2}
   {:note "g", :midi-note 67, :volume 60, :channel 1, :duration 2}
   {:note "f", :midi-note 65, :volume 60, :channel 1, :duration 2}
   {:note "e", :midi-note 64, :volume 60, :channel 1, :duration 2}
   {:note "d", :midi-note 62, :volume 60, :channel 1, :duration 2}
   {:note "c", :midi-note 60, :volume 60, :channel 1, :duration 2}]})
```
The next step is to be able to build the midi track and then play it. This will require taking the above representation and creating a sequence with the respective notes in each channel. 

Outside of that, I'd like to make program changes part of the midi sequence. Finally, I'd like to start writing some tests whenever I figure out how you do that in Clojure.