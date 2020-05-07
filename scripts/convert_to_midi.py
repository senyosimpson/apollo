import argparse
from mido import MidiFile, MidiTrack, Message


def populate_track(track, channel, notes, velocities):
    for note, velocity in zip(notes, velocities):
        track.append(Message('note_on', channel=channel, note=note, velocity=velocity, time=250))
        track.append(Message('note_off', channel=channel, note=note, velocity=velocity, time=250))
    return track
    

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--filename',
                        type=str,
                        required=True,
                        help='The file to convert into midi')
    parser.add_argument('--num-tracks',
                        type=int,
                        required=False,
                        default=2,
                        help='The number of tracks to use')
    args = parser.parse_args()
    filename = args.filename
    num_tracks = args.num_tracks
    
    with open(filename, mode='r') as f:
        notes = f.read().split()
        reversed_notes = notes[::-1]
        notes.extend(reversed_notes)
        notes = [int(note) for note in notes]
        velocities = [60 for _ in range(len(notes))]
    
    midi = MidiFile(type=1)
    for i in range(num_tracks):
        channel = i
        track = MidiTrack()
        track = populate_track(track, channel, notes, velocities)
        midi.tracks.append(track)
    
    midi.save('midifiles/c-major.mid')
