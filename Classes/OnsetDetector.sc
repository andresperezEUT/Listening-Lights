OnsetDetector {

	var <synth;

	*new{ |channel=0, fftSize=512, monitor=0, threshold=0.5, odftype='rcomplex', relaxtime=1, floor=0.1, mingap=10, medianspan=11, whtype=1, rawodf=0|

		^super.new.initOnsetDetector(channel, fftSize, monitor, threshold, odftype, relaxtime, floor, mingap, medianspan, whtype, rawodf);

	}

	initOnsetDetector { |channel=0, fftSize=512, monitor=0, threshold=0.5, odftype='rcomplex', relaxtime=1, floor=0.1, mingap=10, medianspan=11, whtype=1, rawodf=0|


		synth=Synth(\onsetDetector,[\channel,channel, \fftSize,fftSize, \monitor,monitor, \threshold,threshold, \odftype,odftype, \relaxtime,relaxtime, \floor,floor, \mingap,mingap, \medianspan,medianspan, \whtype,whtype, \rawodf,rawodf]);

	}
}