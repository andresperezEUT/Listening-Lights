BeatTracking {

	var <synth;

	*new{ |channel=0, fftSize=1024, monitor=0, krChannel=0, numChannels=5, windowSize=5, phaseaccuracy=0.02, lock=0|

		^super.new.initBeatTracking(channel, fftSize, monitor, krChannel, numChannels, windowSize, phaseaccuracy, lock);

	}

	initBeatTracking { |channel=0, fftSize=1024, monitor=0, krChannel=0, numChannels=5, windowSize=5, phaseaccuracy=0.02, lock=0|


		synth=Synth(\beatTracker,[\channel,channel, \fftSize,fftSize, \monitor,monitor, \krChannel,krChannel, \numChannels,numChannels, \windowSize,windowSize, \phaseaccuracy,phaseaccuracy, \lock,lock]);

	}

	lock {
		synth.set(\lock,1);
	}

	unlock {
		synth.set(\lock,0);
	}

	restart{
		synth.free;
		this.initBeatTracking;
	}
}
