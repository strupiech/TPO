/**
 * @author Strupiechowski Mateusz S18747
 */

package S_PASSTIME_SERVER1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

    private Locale locale;
    private LocalDate firstLocalDate, secondLocalDate;
    private LocalTime firstLocalTime, secondLocalTime;

    public Time(Locale locale) {
        this.locale = locale;
    }

    public static String passed(String firstDate, String secondDate) {
        Time time = new Time(new Locale("PL"));

        if (time.IsWithTime(firstDate) && time.IsWithTime(secondDate)) {
            try {
                LocalDateTime localFirstDate = LocalDateTime.parse(firstDate);
                LocalDateTime localSecondDate = LocalDateTime.parse(secondDate);
                time.firstLocalDate = localFirstDate.toLocalDate();
                time.secondLocalDate = localSecondDate.toLocalDate();
                time.firstLocalTime = localFirstDate.toLocalTime();
                time.secondLocalTime = localSecondDate.toLocalTime();
            } catch (DateTimeParseException e) {
                return "*** " + e.toString();
            }
        } else {
            try {
                time.firstLocalDate = LocalDate.parse(firstDate);
                time.secondLocalDate = LocalDate.parse(secondDate);
            } catch (DateTimeParseException e) {
                return "*** " + e.toString();
            }
        }

        return time.getTimePassedString();
    }

    public boolean IsWithTime(String value) {
        return value.split("T").length > 1;
    }

    public String getTimePassedString() {
        String timePassedString = "";
        timePassedString += getDateToDateDetail(this.firstLocalDate, this.secondLocalDate, this.firstLocalTime, this.secondLocalTime);
        timePassedString += "\n" + getDaysAndWeeksPassed(this.firstLocalDate, this.secondLocalDate);
        if (this.firstLocalTime != null && this.secondLocalTime != null) {
            ZonedDateTime firstZoned = ZonedDateTime.of(LocalDateTime.of(this.firstLocalDate, firstLocalTime), ZoneId.of("Europe/Warsaw"));
            ZonedDateTime secondZoned = ZonedDateTime.of(LocalDateTime.of(this.secondLocalDate, this.secondLocalTime), ZoneId.of("Europe/Warsaw"));
            timePassedString += "\n" + String.format(
                    new Locale("xx"), "- godzin: %d, minut: %d", ChronoUnit.HOURS.between(firstZoned, secondZoned), ChronoUnit.MINUTES.between(firstZoned, secondZoned));
        }
        Period period = Period.between(this.firstLocalDate, this.secondLocalDate);
        if (period.getDays() > 0 || period.getYears() > 0 || period.getMonths() > 0) {
            timePassedString += "\n" + getPassedTimeCalendar(period);
        }
        return timePassedString;
    }

    public String getDateToDateDetail(LocalDate firstDate, LocalDate secondDate, LocalTime firstLocalTime, LocalTime secondLocalTime) throws DateTimeParseException {
        return "Od " + getFormattedDate(firstDate, firstLocalTime) + " do " + getFormattedDate(secondDate, secondLocalTime);
    }

    public String getFormattedDate(LocalDate localDate, LocalTime localTime) {
        if (localTime != null) 
            return LocalDateTime.of(localDate, localTime).format(DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE) 'godz.' HH:mm", this.locale));
        
        return localDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", this.locale));
    }

    public String getDaysAndWeeksPassed(LocalDate firstLocalDate, LocalDate secondLocalDate) {
        float days = ChronoUnit.DAYS.between(firstLocalDate, secondLocalDate);
        return String.format(new Locale("xx"), "- mija: %d dni, tygodni %d", (int) days, (int) days / 7);
    }

    public String getPassedTimeCalendar(Period period) {
        String periodString = "- kalendarzowo: ";
        boolean isYearOrMonthBefore = false;
        if (period.getYears() > 0) {
            if (period.getYears() <= 4) {
                periodString += period.getYears();
                periodString += period.getYears() > 1 ? " lata" : " rok";
            } else {
                periodString += period.getYears();
                periodString += " lat";
            }
            isYearOrMonthBefore = true;
        }
        if (period.getMonths() > 0) {
            if (isYearOrMonthBefore)
                periodString += ", ";
            if (period.getMonths() <= 4) {
                periodString += period.getMonths();
                periodString += period.getMonths() > 1 ? " miesiące" : " miesiąc";
            } else {
                periodString += period.getMonths();
                periodString += " miesięcy";
            }
            isYearOrMonthBefore = true;
        }
        if (period.getDays() > 0) {
            if (isYearOrMonthBefore)
                periodString += ", ";
            periodString += period.getDays();
            periodString += period.getDays() > 1 ? " dni" : " dzień";
        }
        return periodString;
    }

}
