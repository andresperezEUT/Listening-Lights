


RTML.init;

a=OnsetDetector.new(0,monitor:1)
b=OnsetDetector.new(1,monitor:1)

a.synth.class

a.class


b=BeatTracking.new
b.lock
b.unlock
b.restart


p=PitchFollower.new

p.synth.set(\monitor,0)
p.synth.set(\peakThreshold,1)

x=SynthDef(\a,{|freq=100|Out.ar(0,SinOsc.ar(freq))}).play
// MONITOR OSC -- integrar esto en RTML
(
o = OSCFunc({ arg msg, time;
    msg.postln;
	x.set(\freq,msg[3])
},'/tr', s.addr); //receive only from localhost server
)



s.synth