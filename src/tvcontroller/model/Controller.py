from ..states.States import StateCheckTime


class TVController(object):
    def __init__(self) -> None:
        self.state = StateCheckTime()

    def on_event(self, event):
        self.state = self.state.on_event(event)
