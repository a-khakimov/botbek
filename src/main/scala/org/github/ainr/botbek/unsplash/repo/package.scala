package org.github.ainr.botbek.unsplash

package object repo {

  final case class RawStatistics(
      id: String,
      username: String,
      downloads: Downloads
  )

  final case class Downloads(
      total: Long,
      historical: HistoricalDownloads
  )

  final case class HistoricalDownloads(
      values: List[DownloadValue]
  )

  final case class DownloadValue(
      date: String,
      value: Long
  )

  final case class Views(
      total: Long
  )

  final case class Errors(error: List[String])
}
