---
title: "Apollo"
description: "A music programming language built in Clojure"
---

Apollo is a music programming language built in Clojure. It is inspired by [Alda](https://github.com/alda-lang/alda). As I have an affinity for music, I chose this as my first project to get to grips with Clojure. It turned out to be quite an interesting project. Apollo is less ambitious than Alda, only providing basic functionality (and doing so rather inefficiently 😆). I hope you enjoy reading more about it.

## Apollo samples

<br></br>

{{< audio src="/audio/twinkle-twinkle.mp3" class="audio-sample" >}}

## Apollo deep dive

Apollo reads .apl files (they extension is practically useless at this point in time), parses it, builds a midi track and then plays the notes. All the midi heavy lifting is done in Java with Clojure interoperating with it to use it. An .apl file has the general format

```
instrument: octave
notes

instrument: octave
notes

...
```
`instrument` is the instrument name. All the available instruments are defined in the file `instruments.txt`. `octave` denotes the octave within to play. It is specified as `o<number>` (e.g `o4` which will play in the 4th octave). The notes are the letters to play. Multiple instruments can be used by repeating the pattern over again. They do not have to play the same notes, allowing one to get creative with music composition. An example of the C major scale defined in .apl format

```
Acoustic Grand Piano: o4
c d e f g a b c
```

The audio clip of twinkle twinkle little star is defined as 

```
Acoustic Grand Piano: o4
c c g g a a g2 f1 f e e d d c2 g1 g f f e e d2 g1 g f f e e d2 c1 c g g a a g2 f1 f e e d d c2
```

### Notes

To increase the expressivity, notes can be played together and/or have different durations. To play more than one note at a time, they can be written as one "word". For example to play the C major chord

```
ceg
```

As stated, notes can also have varying durations. This is specified by appending a duration at the end of the note. To play the C major chord for a semibreve (4 beats)

```
ceg4
```
Similarly, for a minim we would change `4` to `2`. You get the point 🤫.

When creating a composition, durations only change when they are specified. The default is set to a crotchet (1 beat) and will remain at one 1 beat until a new duration specified. Similarly, the new duration will continue playing until a new duration is specified. For example, say we have the C major composition

```
c d e f g a b c
```

This will play each note for 1 beat. We can modify it as such

```
c d e f2 g a b c
```
which will play `c d e` for 1 beat and `f g a b c` each for 2 beats, because we did not change the duration again. If we had

```
c d e f2 g a4 b1 c
```
we would get `c d e` for 1 beat, `f g` for 2 beats, `a` for 4 beats and `b c` for 1 beat.