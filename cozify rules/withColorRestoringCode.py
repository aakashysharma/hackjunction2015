class DetectMotionAndChangeLight(Rule):
    type = "DETECT_MOTION_CHANGE_LIGHT_RULE"
    name = "Detect Motion and Change light accordingly"
    description = "Detect dance and song"
    summary = "This rule allows you to automatically change light according to the song being played"

    sensors = Input(field_type=None,
                    capabilities=[Capability.MOTION],
                    display_order=1,
                    description="Window/door sensor",
                    min_count=1)
    """ @type: list """

    devices = Output(field_type=Rule.LIGHT,
                     capabilities=[Capability.BRIGHTNESS, Capability.COLOR_HS],
                     display_order=2,
                     description="Controlled devices",
                     min_count=1)
    """ @type: list """

    output_state = Value(field_type=None,
                         display_order=3,
                         description="Turn on and change color",
                         default= 0,
                         min_count=1,
                         max_count=1)
    """ @type: list """

    def __init__(self):
        super().__init__()
        self.sensors_on = set()
        self.turn_on = True
        self.logger.info("INIT...")

    def enable(self):
        self.logger.info("Enable...")
        self.turn_on = self.output_state and self.output_state[0].value
        for t in self.sensors:
            self.send_command(ReportStatusCommand(t))
            

    def send_commands(self, on: bool):
        self.logger.info("Send cmds...")
        for d in self.devices:
          command = DeviceCommand(d)
          command.state = LightState()
          command.state.colorMode = 'hs'
          timeSinceLastChanged = get_timestamp() - command.state.lastChange
          if timeSinceLastChanged > 1000:
            command.state.hue = 1.9
            command.state.saturation = 0.7
          else:
            command.state.hue = 0.56
            command.state.saturation = 0.5
            
          command.state.transitionMsec = 1500
          command.state.brightness = 0.7
          self.send_command(command)

    def get_timestamp(self):
        return self.get_timestamp()
      
    def on_event(self, ev):
        self.logger.info("on event...")
        """if ev.type != ContactEvent.type:
            return"""

        if ev.state.reachable and ev.state.motion:
            self.sensors_on.add(ev.id)
        else:
            self.sensors_on.discard(ev.id)

        self.send_commands(len(self.sensors_on) > 0 if self.turn_on else len(self.sensors_on) == 0)

    def disable(self):
        self.logger.info("Disable...")
        self.sensors_on = set()