We have a demo page already in frontend but it only display. fix it to work with backend
1. Rider app

1.1. Homepage(open first):
mapLibres with openFreeMap, openStreetMap that support drawing route take from requested trip and also let rider to type address and pin point the 2dgeo
A button at the right to locate the self
when done type address and press the show trip it will create a request trip
after that there will be a button to reject or confirm
if reject -> return to map
if confirm -> make a waiting screen till the session is done, fail or expire
if fail, expire then display "fail to match"
if done then proceed to the map and pin point the driver location and display waiting for pickup
when the trip is done then proceed to the payment screen
1.2. Account:
display like the demo
1.3. Trip history:
remove everything, only leave the trip history that the rider make(price, status, date, detail).

2. Driver app: remove the notification
1.1. Homepage(open first):
mapLibres with openFreeMap, openStreetMap 
driver position and display Rider position if in trip
support drawing route take from trip while trip is InProgress
remove the button or div (Quận 1, TP. Hồ Chí Minh 3 khách gần đây)
when press start, the driver enter loading screen that startMatching and start update driver location and polling for a tripOffer
if there is a tripOffer, display it in the middle of the screen(from, to, distance, price) and have a expire count down with confirm or reject button.
if reject then go back to the homepage
if confirm then start displaying riderPosition(from) and change the div that contain start button to display trip information and change the start button to pickedup
if the driver press pickedup then proceed to complete
if complete then in waiting screen till the trip is PAID
1.2. Account:
display like the demo but remove:
- section (chuyen di, danh gia, ty le hoan thanh)
- section (ho so an tuong)
- button (cai dat)
- section (Phuong tien only display 1 driverLicense, 1 licensePlate)
1.3. Statistic:
display like the demo but remove (danh gia trung binh), change the trip history driver take to only display (price, status, date, detail)
remove the (Thu Nhap button)