<?
include_once("../header.php");
include_once("inc/td_core.php");
ob_clean();
$WEATHER_CITY = td_iconv($WEATHER_CITY, "utf-8", $MYOA_CHARSET);

if($WEATHER_CITY != "")
{
   $WEATHER_INFO = tdoa_weather($WEATHER_CITY, "e");
   if(substr($WEATHER_INFO, 0, 6) == "error:")
      $WEATHER_INFO = substr($WEATHER_INFO, 6);
	
	
 //  echo '<div class="weather-result"><br>'.$WEATHER_INFO.'</div>';
	ob_clean();
	
	echo $WEATHER_INFO;
}
?>