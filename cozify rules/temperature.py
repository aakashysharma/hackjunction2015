class AACustomChangeLightsToMusic(Rule):
    type = "AACUSTOM_ONOFF_CONTACT_RULE"
    name = "Change Lights temp"
    description = "Change Lights according to Music genre"
    summary = "This rule allows you to just play a song and the lights will change accordingly"

    sensors = Input(field_type=None,
                    capabilities=[Capability.MOTION],
                    display_order=1,
                    description="Receiver for notification",
                    min_count=1)
    """ @type: list """

    devices = Output(field_type=None,
                     capabilities=[Capability.COLOR_TEMP, Capability.BRIGHTNESS],
                     display_order=2,
                     description="Controllable lights",
                     min_count=1)
    """ @type: list """

    output_state = Value(field_type=Rule.PERCENTAGE,
                         display_order=3,
                         description="Change lights",
                         default= 0.2,
                         min_count=1,
                         max_count=1)
    """ @type: list """

    def __init__(self):
        super().__init__()
        self.sensors = set()
        self.turn_on = True

    def enable(self):
        self.turn_on = self.output_state and self.output_state[0].value
        for t in self.sensors:
            self.send_command(ReportStatusCommand(t))

    def send_commands(self, on: bool):
        for d in self.devices:
			command = DeviceCommand(d)
			command.state = LightState()
			command.state.brightness = 0.7
			command.state.temperature = 2700
			self.send_command(command)

    def on_event(self, ev):
        if ev.type != MotionEvent.type:
            return

        if ev.state.reachable and ev.state.motion:
            self.sensors.add(ev.id)
        else:
            self.sensors.discard(ev.id)

        self.send_commands(len(self.sensors) > 0 if self.turn_on else len(self.sensors) == 0)

    def disable(self):
        self.sensors = set()