# Trips screen API setup

The third bottom-navigation icon now opens the Trips screen.

## Configure the remote backend

In `app/build.gradle.kts`, replace:

```kotlin
buildConfigField("String", "TRIPS_API_BASE_URL", "\"https://YOUR_API_HOST/\"")
buildConfigField("String", "VEHICLE_HEALTH_VIN", "\"MA1ABC12345678901\"")
```

with the actual remote API root URL (including trailing slash) and the VIN for this vehicle. Example URL shape only: `https://api.example.com/`.

The app calls:
- `GET api/v1/vehicles/{vin}/trips` when the Trips screen opens and when Retry is tapped.
- `GET api/v1/vehicles/{vin}/trips/{tripId}` when a trip card is tapped.
- `POST api/v1/vehicles/{vin}/trips` is defined in `TripApi` and `TripRepository` for saving trip data; this screen does not invoke it because the supplied UI has no add/save trip action.

The map is a locally drawn dummy map and does not call a mapping service. Search filters the loaded trips by `startAddress` and `endAddress` locally.

The provided API screenshots do not include authentication requirements or a full response envelope schema. The implementation currently expects API 6 to return a JSON array of trip objects and API 7 / API 5 to return one trip object, matching the sample response structure.
