package org.github.ainr.botbek.unsplash

package object repo {

  final case class RawStatistics(
      id: String,
      username: String,
      downloads: RawDownloads,
      views: RawViews
  )

  final case class RawDownloads(
      total: Long,
      historical: RawHistoricalDownloads
  )

  final case class RawHistoricalDownloads(
      values: List[RawDownloadValue]
  )

  final case class RawDownloadValue(
      date: String,
      value: Long
  )

  final case class RawViews(
      total: Long,
      historical: RawHistoricalViews
  )

  final case class RawHistoricalViews(
      values: List[RawViewValue]
  )

  final case class RawViewValue(
      date: String,
      value: Long
  )

  final case class Errors(error: List[String])
}
