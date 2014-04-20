RTML {

	*init {

		//////////////////////////////////////////////////////////////////////////////////////////
		// ONSET DETECTOR
		//////////////////////////////////////////////////////////////////////////////////////////

		SynthDef(\onsetDetector,{ |channel=0, fftSize=512, monitor=0, threshold=0.5, odftype='rcomplex', relaxtime=1, floor=0.1, mingap=10, medianspan=11, whtype=1, rawodf=0|

			var in, fft, onsets;
			var id, value;

			// osc message info
			id=channel;
			value=0;

			// onset detection
			in = SoundIn.ar(channel);
			fft= FFT(LocalBuf(fftSize), in);
			onsets = Onsets.kr(fft,threshold,odftype,relaxtime,floor,mingap,medianspan,whtype,rawodf);
			SendTrig.kr(onsets,id,value);

			// monitor signal
			Out.ar(0, Pan2.ar(in,level:monitor));

		}).add;



		//////////////////////////////////////////////////////////////////////////////////////////
		// BEAT TRACKER
		//////////////////////////////////////////////////////////////////////////////////////////

		SynthDef(\beatTracker,{ | channel=0, fftSize=1024, monitor=0, krChannel=0, numChannels=5, windowSize=5, phaseaccuracy=0.02, lock=0|
			var in, kbus;
			var trackb,trackh,trackq,tempo, phase, period, groove;
			var bsound,hsound,qsound, beep;
			var fft;
			var feature1, feature2, feature3, feature4;

			in = SoundIn.ar(channel);

			//Create some features
			fft= FFT(LocalBuf(fftSize), in);

			feature1= RunningSum.rms(in,64);
			feature2= MFCC.kr(fft,2); //two coefficients
			feature3= A2K.kr(LPF.ar(in,1000));
			feature4= Onsets.kr(fft);

			kbus= Out.kr(krChannel, [feature1, feature3]++feature2++feature4);

			#trackb,trackh,trackq,tempo, phase, period, groove=BeatTrack2.kr(krChannel,numChannels,windowSize, phaseaccuracy, lock, -2.5);

			// beats, subbeats with tempo (1=60bpm)
			SendTrig.kr(trackb,0,tempo);
			SendTrig.kr(trackh,1,tempo);

			Out.ar(0, Pan2.ar(in,level:monitor));

		}).add;



		//////////////////////////////////////////////////////////////////////////////////////////
		// MONOPHONIC PITCH DETECTION (time autocorrelation based)
		//////////////////////////////////////////////////////////////////////////////////////////

		SynthDef(\pitchMono,{ |channel=0, monitor=0, initFreq=440, minFreq=60, maxFreq=4000, execFreq=100, maxBinsPerOctave=16, median=1, ampThreshold=0.01, peakThreshold=0.5, downSample=1, clar=0|

			var in,  freq, hasFreq, out;
			in = SoundIn.ar(channel);

			# freq, hasFreq = Pitch.kr(in, initFreq, minFreq, maxFreq, execFreq, maxBinsPerOctave, median, ampThreshold, peakThreshold, downSample, clar);

			// OSC -- TODO!
			SendTrig.kr(hasFreq,0,freq);
			SendTrig.kr(1-hasFreq,0,0);  //this can be used as a kinda noteOff


			// monitor signal
			Out.ar(0, Pan2.ar(in,level:monitor));

		}).add;





	}

}