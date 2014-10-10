RTMLtracker {

	var <nodeID;
	var <synth;
	var <>testSound;

	var <>delta = 0.2;
	var <>msgType;

	var <name;
	var <oscMsgName;
	var <>send=false;

	var parameters;

	*new { |channel, monitor|
		^super.new.initSynth(channel, monitor).initRTMLtracker;
	}

	initRTMLtracker {

		nodeID = synth.nodeID;
		name = RTML.addTracker(this); //given by the RTML number of instances of this type
		oscMsgName = "/rtml" +/+ name;

		testSound = false;
		nodeID = synth.nodeID;

		^name;
	}

	get { |parameter|
		^parameters.at(parameter);
	}

	getAllParameters {
		^parameters.getPairs;
	}

	set { |parameter, value|
		// if parameter exists
		if (parameters.keys.asArray.indexOf(parameter).isNil.not) {
			//change value
			parameters.put(parameter,value);
			//set synth
			synth.set(parameter,value);
		} {
			("Parameter " ++ parameter ++ " not defined").warn;
		}
	}

	start {
		synth.run(true);
	}

	stop {
		synth.run(false);
	}

	//public
	testMsg {
		this.sendMsg;
	}

}



OnsetDetector : RTMLtracker {

	*new{ |channel = 0, monitor = 0|
		^super.new(channel,monitor);
	}

	initSynth { |channel, monitor|

		msgType = \flash;
		parameters = Dictionary.newFrom([\channel,channel, \monitor,monitor, \fftSize,512,  \threshold,0.5, \odftype,'rcomplex', \relaxtime,1, \floor,0.1, \mingap,10, \medianspan,11, \whtype,1, \rawodf,0]);

		synth = Synth(\onsetDetector,parameters.getPairs);
	}

	// private
	sendMsg {
		switch (msgType)
		{\button} {
			// sending a 1, alternatively open and close button
			RTML.destAddr.sendMsg(oscMsgName,1);
			{RTML.destAddr.sendMsg(oscMsgName,1)}.defer(delta); // TODO: creo que no va!
		}
		{\flash} {
			// sending a 1 open, sending a 0 close
			RTML.destAddr.sendMsg(oscMsgName,1);
			{RTML.destAddr.sendMsg(oscMsgName,0)}.defer(delta);
		}
		{\random} {
			RTML.destAddr.sendMsg("/rtml/onset_"++4.rand,1);
		}

	}
}


BeatTracker : RTMLtracker {

	*new{ |channel = 0, monitor = 0|
		^super.new(channel,monitor);
	}

	initSynth { |channel, monitor|

		msgType = \flash;
		parameters = Dictionary.newFrom([\channel,channel, \monitor,monitor, \fftSize,1024, \krChannel,0, \numChannels,5, \windowSize,5, \phaseaccuracy,0.02, \lock,0]);

		synth = Synth(\beatTracker,parameters.getPairs);


	}

	reset {
		synth.free;
		synth = Synth(\beatTracker,parameters.getPairs);
		nodeID = synth.nodeID;
		RTML.nodeIDs.add(name.asSymbol -> nodeID);
	}

	// private
	sendMsg {
		switch (msgType)
		{\button} {
			// sending a 1, alternatively open and close button
			RTML.destAddr.sendMsg(oscMsgName,1);
			{RTML.destAddr.sendMsg(oscMsgName,1)}.defer(delta);
		}
		{\flash} {
			// sending a 1 open, sending a 0 close
			RTML.destAddr.sendMsg(oscMsgName,1);
			{RTML.destAddr.sendMsg(oscMsgName,0)}.defer(delta);
		}

	}
}


PitchFollower : RTMLtracker {

	var <>mode = \pitch1;
	var <toneSynth;


	*new{ |channel = 0, monitor = 0|
		^super.new(channel,monitor);
	}

	initSynth { |channel, monitor|

		msgType = \flash;/////////////
		parameters = Dictionary.newFrom([\channel,channel, \monitor,monitor, \initFreq,440, \minFreq,60, \maxFreq,4000, \execFreq,100, \maxBinsPerOctave,16, \median,1, \ampThreshold,0.01, \peakThreshold,0.5, \downSample,1, \clar,0, \replayRate,20]);

		synth = Synth(\pitchMono,parameters.getPairs);
		toneSynth = Synth.newPaused(\tone);
	}

	sendMsg { |value=0|
		RTML.destAddr.sendMsg(oscMsgName,value.asInt);
	}

	testSound_ { |value|
		testSound = value;
		if (value) {
			toneSynth.run;
		} {
			toneSynth.run(false);
		}
	}
}

PeakTracker : RTMLtracker {

	*new{ |channel = 0, monitor = 0|
		^super.new(channel,monitor);
	}

	initSynth { |channel, monitor|

		parameters = Dictionary.newFrom([\channel,channel, \monitor,monitor, \replyRate,20, \lagTime,0.1, \decay,0.999, \gain, 1]);

		synth = Synth(\peakFollower,parameters.getPairs);
	}

	sendMsg { |value=0|
	RTML.destAddr.sendMsg(oscMsgName,value.asInt);
		NetAddr.localAddr.sendMsg(oscMsgName,value.asInt);
	}
}
