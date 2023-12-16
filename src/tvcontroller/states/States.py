import subprocess
import psutil
import time, requests, json
from .MainState import MainState
from datetime import datetime

class STATE_CHECK_TIME(MainState):
    """
    Check if is late night, between midnight and 2 am
    """
    def isLateTime(self, currentHour: int):
        return (currentHour >= 0 and currentHour <= 2)

    """
    Check if is time to show anything
    """
    def isTimeToShow(self, currentHour: int):
        current_datetime = datetime.now()
        current_day_of_week = current_datetime.weekday()

        match current_day_of_week:
            case 0,1,2,3,4:
                print(" -- It's week day")
                if (currentHour >= 16 or self.isLateTime(currentHour)):
                    return True
            case _:
                print(" -- It's weekend")
                if (currentHour >= 8 or self.isLateTime(currentHour)):
                    return True
                return False

    """
    Check if the current time is correct to show any episodes/moveis based of the week day
    """
    def on_event(self, event):
        print(" - Current state: STATE_CHECK_TIME")
        time.sleep(3)
        currentHour = datetime.now().hour
        if self.isTimeToShow(currentHour):
            STATE_GET_VIDEO.on_event(self, None)
        else:
            STATE_OFFLINE.on_event(self, None)    
        return self

class STATE_GET_VIDEO(MainState):
    """
    Get current time in UTC
    """
    def getCurrentTimeUtc():
        current_time = datetime.utcnow()
        formatted_time = current_time.strftime('%Y-%m-%dT%H:%M:%SZ')
        return formatted_time

    """
    Make a http request to get the next episode/video to show
    """
    def on_event(self, event):
        print(" - Current state: STATE_GET_VIDEO")
        time.sleep(2)
        try:
            currentTime = STATE_GET_VIDEO.getCurrentTimeUtc()
            url = "http://localhost:8080/api/tv/nextShow?time="+str(currentTime)
            response = requests.get(url)

            print(response)

            if response.status_code == 200:
                STATE_INTERMISSION.on_event(self, response.text)
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
        print(" - Current state: STATE_INTERMISSION")
        data = json.loads(event)
        print(data["duration"])
        print(data["seriesName"])
        print(data["episode"])
        print(data["season"])
        time.sleep(2)
        STATE_SHOW.on_event(self, None)
        return self

class STATE_SHOW(MainState):
    def is_vlc_running():
        for process in psutil.process_iter(['pid', 'name']):
            if 'vlc' in process.info['name'].lower():
                return True
        return False

    """
    Show the requestes show/movie
    """
    def on_event(self, event):
        print(" - Current state: STATE_SHOW")

        # URL or path to the media file you want to play
        media_url = "..\video.mp4"  # or "C:\\path\\to\\your\\file.mp4"

        command = ["vlc", "--play-and-exit", media_url]

        # Use subprocess to execute the command in the command line
        subprocess.run(command)

#        time.sleep(2)
#        STATE_GET_VIDEO.on_event(self, None)
        return self

class STATE_OFFLINE(MainState):
    """
    Idle state.
    """
    def on_event(self, event):
        print(" - Current state: STATE_OFFLINE")
        print(" -- In idle... waiting 60 seconds")
        time.sleep(60)
        STATE_CHECK_TIME.on_event(self, None)
        return self
