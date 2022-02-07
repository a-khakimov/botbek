package org.github.ainr.botbek.unsplash.service

trait UnsplashService {
  def getUserStatistics(user: String): Statistics
}
