package org.github.ainr.botbek.unsplash

import org.github.ainr.botbek.unsplash.repo.RawStatistics

import java.time.LocalDate
import java.time.format.DateTimeFormatter

package object service {

  final case class Statistics(
      id: String,
      username: String,
      downloads: Downloads,
      views: Views
  )

  object Statistics {

    def apply(raw: RawStatistics): Statistics = Statistics(
      id = raw.id,
      username = raw.username,
      downloads = Downloads(
        total = raw.downloads.total,
        historical = HistoricalDownloads(
          values = raw.downloads.historical.values.map {
            value =>
              DownloadValue(
                LocalDate.parse(value.date, dateTimeFormatter),
                value.value
              )
          }
        )
      ),
      views = Views(
        total = raw.views.total,
        historical = HistoricalViews(
          values = raw.views.historical.values.map {
            value =>
              ViewValue(
                LocalDate.parse(value.date, dateTimeFormatter),
                value.value
              )
          }
        )
      )
    )
  }

  final case class Downloads(
      total: Long,
      historical: HistoricalDownloads
  )

  final case class HistoricalDownloads(
      values: List[DownloadValue]
  )

  final case class DownloadValue(
      date: LocalDate,
      value: Long
  )

  final case class Views(
      total: Long,
      historical: HistoricalViews
  )

  final case class HistoricalViews(
      values: List[ViewValue]
  )

  final case class ViewValue(
      date: LocalDate,
      value: Long
  )

  private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd")
}
