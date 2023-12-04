from states.States import STATE_CHECK_TIME

class TVController(object):
    def __init__(self) -> None:
        self.state = STATE_CHECK_TIME()

    def on_event(self, event):
        self.state = self.state.on_event(event)
