PitchFollower {

	var <synth;

	*new{ |channel=0, monitor=0, initFreq=440, minFreq=60, maxFreq=4000, execFreq=100, maxBinsPerOctave=16, median=1, ampThreshold=0.01, peakThreshold=0.5, downSample=1, clar=0|

		^super.new.initPitchFollower();

	}

	initPitchFollower { |channel=0, monitor=0, initFreq=440, minFreq=60, maxFreq=4000, execFreq=100, maxBinsPerOctave=16, median=1, ampThreshold=0.01, peakThreshold=0.5, downSample=1, clar=0|


		synth=Synth(\pitchMono,[\channel,channel, \monitor,monitor, \initFreq,initFreq, \minFreq,minFreq, \maxFreq,maxFreq, \execFreq,execFreq, \maxBinsPerOctave,maxBinsPerOctave, \median,median, \ampThreshold,ampThreshold, \peakThreshold,peakThreshold, \downSample,downSample,  \clar,clar]);

	}

}
