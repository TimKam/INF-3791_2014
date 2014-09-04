A Mobile Application for Managing Climate/Pollution Threads for COPD Patients
=============================================================================

The aim of this exercise (seems to be) is to develop (conceptually and technically) a prototype for a mobile COPD management app.
To get a basic impression of the disease, read:
http://en.wikipedia.org/wiki/Chronic_obstructive_pulmonary_disease
The app should help COPD patients to be aware of potential threads to their health due to weather conditions and air pollution.
E.g. should the patients easily see which areas in their environment are currently and in the near future imposing health threads.

A possible architectural and technological approach is as follows:

The main app is a native Android application (Java for Android) and servers as a fairly "thick" client which is getting relevant data from
several third party services (Statens Vegvesen, Meteorologisk Institutt, etc...) as XML and/or JSON through RESTful HTTP APIs.

The app is then aggregating the data and displaying it to the user in a purposeful way (e.g. through getting the users location and through comparing the air
air pollution in this location with threshold values).

Furthermore, one could set up a web server collecting user data (they could e.g. document when and where they encountered health problems) and analyzing the data in hindsight of environmental data
in order to improve the information value of location, climate and pollution data.

Own conceptual and technological approaches are encouraged.


Helpful Technical Links & Resources:
* Android Development:
	Installation:
	https://developer.android.com/sdk/installing/index.html
	
	Tutorials:
	http://developer.android.com/training/index.html
	http://www.youtube.com/watch?v=SUOWNXGRc6g&list=PL023BC9408BAFEC0C

* API Documentation:
	Weather:
	http://api.met.no/weatherapi/locationforecast/1.9/documentation
	Traffic:
	https://www.vegvesen.no/nvdb/api/dokumentasjon
