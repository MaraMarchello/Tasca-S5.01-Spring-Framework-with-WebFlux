# Blackjack Frontend

A simple HTML/CSS/JavaScript frontend for the Blackjack API.

## Features

- **Player Management**: Create new players or load existing ones
- **Game Interface**: Visual card display with proper suits and ranks
- **Game Controls**: Hit, Stand, Split, and Insurance options
- **Real-time Statistics**: Track wins, losses, and balance
- **Responsive Design**: Works on desktop and mobile devices

## How to Use

### 1. Start the Backend
Make sure your Blackjack API is running on `http://localhost:8080`:

```bash
cd blackjack-api
./gradlew bootRun
```

### 2. Open the Frontend
Simply open `index.html` in your web browser. You can:
- Double-click the file in your file explorer
- Or serve it with a simple HTTP server:

```bash
# Using Python (if installed)
python -m http.server 8000

# Using Node.js (if installed)
npx http-server

# Then open http://localhost:8000 in your browser
```

### 3. Play the Game

1. **Create or Load Player**:
   - Enter username and email to create a new player
   - Or enter just username to load an existing player

2. **Start Playing**:
   - Set your bet amount
   - Click "Start Game"
   - Use Hit/Stand buttons to play
   - Split and Insurance options appear when available

3. **View Statistics**:
   - Your stats update automatically after each game
   - Balance, games played, win rate, and total winnings are tracked

## Game Rules

- **Objective**: Get as close to 21 as possible without going over
- **Card Values**: 
  - Number cards: Face value
  - Face cards (J, Q, K): 10 points
  - Aces: 1 or 11 (automatically optimized)
- **Blackjack**: 21 with first two cards
- **Bust**: Hand value over 21 (automatic loss)

## Technical Details

- **Frontend**: Pure HTML5, CSS3, and JavaScript (ES6+)
- **API Integration**: Uses Fetch API to communicate with Spring WebFlux backend
- **Responsive**: CSS Grid and Flexbox for responsive layout
- **No Dependencies**: No external libraries required

## Browser Compatibility

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## File Structure

```
frontend/
├── index.html      # Main HTML file
├── styles.css      # CSS styling
├── script.js       # JavaScript logic
└── README.md       # This file
```

## API Endpoints Used

- `POST /api/players` - Create player
- `GET /api/players/username/{username}` - Load player
- `GET /api/players/{id}/stats` - Get player statistics
- `POST /api/games` - Start new game
- `POST /api/games/{gameId}/hit` - Hit action
- `POST /api/games/{gameId}/stand` - Stand action
- `POST /api/games/{gameId}/split` - Split action
- `POST /api/games/{gameId}/insurance` - Insurance action

## Troubleshooting

**CORS Issues**: If you see CORS errors in the browser console, make sure:
1. The backend is running with CORS enabled (already configured)
2. You're accessing the frontend via HTTP (not file://)

**API Connection**: If API calls fail:
1. Verify the backend is running on port 8080
2. Check the browser console for error messages
3. Ensure the database containers are running

**Player Not Found**: If loading an existing player fails:
1. Make sure you've created the player first
2. Check the exact username spelling
3. Verify the player exists in the database 