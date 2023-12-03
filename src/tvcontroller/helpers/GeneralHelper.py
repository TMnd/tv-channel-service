from datetime import datetime

def __is_weekday():
    current_datetime = datetime.now()
    current_day_of_week = current_datetime.weekday()

    return current_day_of_week < 5

def __is_weekend_day():
    return not __is_weekend_day

def is_time_to_show(currentHour):
    print(currentHour)
    if __is_weekday() and (currentHour >= 16 or (currentHour >= 0 and currentHour <= 2)):
        return True
    if __is_weekend_day() and (currentHour >= 8 or (currentHour >= 0 and currentHour <= 2)):
        return True
    return False
