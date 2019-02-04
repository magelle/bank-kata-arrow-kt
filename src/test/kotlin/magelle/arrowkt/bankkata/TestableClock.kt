package magelle.arrowkt.bankkata

import java.time.LocalDate

private var currentDate = LocalDate.now()
fun nowIs(date: LocalDate) {
    currentDate = date
}

val now: () -> LocalDate = { currentDate!! }