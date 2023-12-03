import time, requests
from .MainState import MainState
from datetime import datetime
from helpers.GeneralHelper import is_time_to_show

class STATE_CHECK_TIME(MainState):
    """
    Check if the current time is correct to show any episodes/moveis based of the week day
    """
    def on_event(self, event):
        print("Current state: STATE_CHECK_TIME")
        time.sleep(3)
        currentHour = datetime.now().hour
        if is_time_to_show(currentHour):
            STATE_GET_VIDEO.on_event(self, None)
        else:
            STATE_OFFLINE.on_event(self, None)    
        return self

class STATE_GET_VIDEO(MainState):
    """
    Make a http request to get the next episode/video to show
    """
    def on_event(self, event):
        print("state get video")
        time.sleep(2)
        try:
            response = requests.get("")

            if response.status_code == 200:
                STATE_INTERMISSION.on_event(self, response)
            else:
                STATE_OFFLINE.on_event(self, response)

        except Exception as e:
            print(f"An error occurred: {e}")
            STATE_OFFLINE.on_event(self, None)

        return self
    
class STATE_INTERMISSION(MainState):
    """
    Show intermission beetwen shows
    """
    def on_event(self, event):
        print("state intermission")
        time.sleep(2)
        STATE_SHOW.on_event(self, None)
        return self

class STATE_SHOW(MainState):
    """
    Show the requestes show/movie
    """
    def on_event(self, event):
        print("state show")
        time.sleep(2)
        STATE_GET_VIDEO.on_event(self, None)
        return self

class STATE_OFFLINE(MainState):
    """
    Idle state.
    """
    def on_event(self, event):
        print("In idle... waiting 60 seconds")
        time.sleep(60)
        STATE_CHECK_TIME.on_event(self, None)
        return self
