package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.ArrayList;
import java.util.List;

/**
 * An object to store lists of the change in the players list of an instance.
 *
 *
 */
public class PlayerListDelta {

  private List<String> playersRemoved;
  private List<String> playersAdded;

  public static PlayerListDelta NO_CHANGE =
    new PlayerListDelta(new ArrayList<String>(), new ArrayList<String>());

  /**
   * Creates a PlayerListDelta with the specified player change lists.
   * @param playersRemoved A list of the player id's of players that have
   * left the game since the last update.
   * @param playersAdded A list of the player id's of players that have
   * joined the game since the last update.
   */
  public PlayerListDelta(List<String> playersRemoved, List<String> playersAdded) {
    this.playersRemoved = playersRemoved;
    this.playersAdded = playersAdded;
  }

  /**
   * Get the list of players that have joined the instance since the last
   * update.
   * @return A list of email addresses of new players.
   */
  public List<String> getPlayersAdded(){
    return playersAdded;
  }

  /**
   * Get the list of players that have left the instance since the last
   * update.
   * @return A list of email addresses of players that have left.
   */
  public List<String> getPlayersRemoved(){
    return playersRemoved;
  }
}
