package org.github.ainr.botbek.unsplash

package object service {

  final case class Statistics(
      id: String,
      username: String,
      downloads: List[Download],
      views: List[View]
  )

  final case class Download(
      total: Long
  )

  final case class View(
      total: Long
  )
}
