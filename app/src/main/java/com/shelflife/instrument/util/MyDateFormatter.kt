package com.shelflife.instrument.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object MyDateFormatter {

    //проверяем валидность даты в формате дд/мм/гггг
    fun parseDateOrDefault(dateString: String): Triple<Int, Int, Int> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false // Включаем строгую проверку даты

        return try {
            // Попытка распарсить дату из строки
            if(dateString.length<10) throw ParseException("",0)
            val date = dateFormat.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date

            // Извлекаем день, месяц и год
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) // Добавляем 1, так как месяцы начинаются с 0
            val year = calendar.get(Calendar.YEAR)

            Triple(day, month, year)
        } catch (e: ParseException) {
            // В случае невалидной даты, используем текущую дату
            val calendar = Calendar.getInstance()

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) // Добавляем 1, так как месяцы начинаются с 0
            val year = calendar.get(Calendar.YEAR)

            Triple(day, month, year)
        }
    }

    fun convertDateFormat(dateStr: String): String {
        // Формат входной даты
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Формат выходной даты
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(dateStr) // Парсинг строки в дату
            outputFormat.format(date) // Форматирование даты в новую строку
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun convertFromDateFormat(dateStr: String): String{
        // Формат входной даты
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Формат выходной даты
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(dateStr) // Парсинг строки в дату
            outputFormat.format(date) // Форматирование даты в новую строку
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun calculateTimeUntil(targetDate: String): Triple<Int, Int, Int> {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val futureDate = sdf.parse(targetDate)
        val currentDate = Calendar.getInstance().time

        if(currentDate>futureDate){
            return Triple(0,0,0)
        }

        val futureCal = Calendar.getInstance().apply { time = futureDate }
        val currentCal = Calendar.getInstance().apply { time = currentDate }

        var years = futureCal.get(Calendar.YEAR) - currentCal.get(Calendar.YEAR)
        var months = futureCal.get(Calendar.MONTH) - currentCal.get(Calendar.MONTH)
        var days = futureCal.get(Calendar.DAY_OF_MONTH) - currentCal.get(Calendar.DAY_OF_MONTH)

        // Корректируем значения, если дни или месяцы отрицательные
        if (days < 0) {
            months -= 1
            val maxDayInPrevMonth = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            days += maxDayInPrevMonth
        }

        if (months < 0) {
            years -= 1
            months += 12
        }

        return Triple(years, months, days)
    }

    fun calculateDaysUntil(targetDate: String): Long {
        // Формат даты: yyyy-MM-dd
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val futureDate = sdf.parse(targetDate)

        // Получаем текущую дату
        val currentDate = Calendar.getInstance().time

        // Вычисляем разницу во времени (в миллисекундах)
        val diffInMillis = futureDate.time - currentDate.time

        // Конвертируем миллисекунды в дни
        var diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        // Если разница больше нуля и остались миллисекунды, увеличиваем diffInDays
        if (diffInMillis > 0 && diffInMillis % TimeUnit.DAYS.toMillis(1) != 0L) {
            diffInDays++
        }

        return diffInDays
    }

    fun formatUnixTime(unixTime: Long): String {
        // Преобразуем Unix-время в миллисекундах в Date объект
        val date = Date(unixTime)

        // Создаем форматтер для нужного формата
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Форматируем дату
        return sdf.format(date)
    }

    fun getTimeInt(timeStr: String): Pair<Int, Int>{
        val times = timeStr.split(":")
        return try {
            Pair(times[0].toInt(), times[1].toInt())
        }catch (_:Exception){
            Pair(0,0)
        }
    }
}