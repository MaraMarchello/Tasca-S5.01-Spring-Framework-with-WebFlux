// Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Global state
let currentPlayer = null;
let currentGame = null;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('username').focus();
});

// Utility functions
function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function showMessage(message, type = 'info') {
    const messageEl = document.getElementById('playerMessage');
    messageEl.textContent = message;
    messageEl.className = `message ${type}`;
    messageEl.style.display = 'block';
    
    setTimeout(() => {
        messageEl.style.display = 'none';
    }, 5000);
}

// API request helper
async function apiRequest(endpoint, options = {}) {
    showLoading();
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    } finally {
        hideLoading();
    }
}

// Player management
async function createPlayer() {
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();

    if (!username || !email) {
        showMessage('Please enter both username and email', 'error');
        return;
    }

    try {
        const player = await apiRequest('/players', {
            method: 'POST',
            body: JSON.stringify({ username, email })
        });

        currentPlayer = player;
        showMessage('Player created successfully!', 'success');
        showGameSection();
    } catch (error) {
        showMessage(`Error creating player: ${error.message}`, 'error');
    }
}

async function loadExistingPlayer() {
    const username = document.getElementById('username').value.trim();

    if (!username) {
        showMessage('Please enter username to load existing player', 'error');
        return;
    }

    try {
        const player = await apiRequest(`/players/username/${encodeURIComponent(username)}`);
        currentPlayer = player;
        showMessage('Player loaded successfully!', 'success');
        showGameSection();
    } catch (error) {
        showMessage(`Error loading player: ${error.message}`, 'error');
    }
}

function showGameSection() {
    document.getElementById('playerSection').style.display = 'none';
    document.getElementById('gameSection').style.display = 'block';
    document.getElementById('playerInfo').style.display = 'block';
    
    updatePlayerInfo();
    updatePlayerStats();
}

function updatePlayerInfo() {
    if (currentPlayer) {
        document.getElementById('playerName').textContent = currentPlayer.username;
        document.getElementById('playerBalance').textContent = currentPlayer.balance.toFixed(2);
    }
}

async function updatePlayerStats() {
    if (!currentPlayer) return;

    try {
        const stats = await apiRequest(`/players/${currentPlayer.id}/stats`);
        document.getElementById('gamesPlayed').textContent = stats.gamesPlayed;
        document.getElementById('gamesWon').textContent = stats.gamesWon;
        document.getElementById('winRate').textContent = `${(stats.winRate * 100).toFixed(1)}%`;
        document.getElementById('totalWinnings').textContent = stats.totalWinnings.toFixed(2);
        
        currentPlayer.balance = stats.balance;
        updatePlayerInfo();
    } catch (error) {
        console.error('Error updating player stats:', error);
    }
}

// Game functions
async function startNewGame() {
    const betAmount = parseFloat(document.getElementById('betAmount').value);
    
    if (!betAmount || betAmount <= 0) {
        showMessage('Please enter a valid bet amount', 'error');
        return;
    }

    if (betAmount > currentPlayer.balance) {
        showMessage('Insufficient balance for this bet', 'error');
        return;
    }

    try {
        const game = await apiRequest('/games', {
            method: 'POST',
            body: JSON.stringify({
                playerId: currentPlayer.id,
                bet: betAmount
            })
        });

        currentGame = game;
        showGameBoard();
        updateGameDisplay();
    } catch (error) {
        showMessage(`Error starting game: ${error.message}`, 'error');
    }
}

function showGameBoard() {
    document.getElementById('betSection').style.display = 'none';
    document.getElementById('gameBoard').style.display = 'block';
    document.getElementById('gameStatus').style.display = 'none';
}

function updateGameDisplay() {
    if (!currentGame) return;

    displayHand(currentGame.playerHand, 'playerHand', 'playerValue', false);
    displayHand(currentGame.dealerHand, 'dealerHand', 'dealerValue', true);
}

function displayHand(hand, containerId, valueId, isDealerHand = false) {
    const container = document.getElementById(containerId);
    const valueElement = document.getElementById(valueId);
    
    container.innerHTML = '';
    
    if (!hand || !hand.cards) return;

    hand.cards.forEach((card, index) => {
        const isHidden = isDealerHand && index === 1 && currentGame.status === 'IN_PROGRESS';
        const cardElement = createCardElement(card, isHidden);
        container.appendChild(cardElement);
    });

    if (isDealerHand && currentGame.status === 'IN_PROGRESS' && hand.cards.length > 1) {
        valueElement.textContent = '?';
    } else {
        valueElement.textContent = calculateHandValue(hand.cards);
    }
}

function createCardElement(card, isHidden = false) {
    const cardDiv = document.createElement('div');
    cardDiv.className = 'card';
    
    if (isHidden || !card.faceUp) {
        cardDiv.classList.add('back');
        cardDiv.innerHTML = '<div>🂠</div>';
    } else {
        const isRed = card.suit === 'HEARTS' || card.suit === 'DIAMONDS';
        if (isRed) cardDiv.classList.add('red');
        
        const suit = getSuitSymbol(card.suit);
        const rank = getRankSymbol(card.rank);
        
        cardDiv.innerHTML = `
            <div class="card-rank">${rank}</div>
            <div class="card-suit">${suit}</div>
        `;
    }
    
    return cardDiv;
}

function getSuitSymbol(suit) {
    const suits = {
        'HEARTS': '♥',
        'DIAMONDS': '♦',
        'CLUBS': '♣',
        'SPADES': '♠'
    };
    return suits[suit] || suit;
}

function getRankSymbol(rank) {
    const ranks = {
        'ACE': 'A',
        'JACK': 'J',
        'QUEEN': 'Q',
        'KING': 'K'
    };
    return ranks[rank] || rank;
}

function calculateHandValue(cards) {
    let value = 0;
    let aces = 0;
    
    cards.forEach(card => {
        if (card.faceUp !== false) {
            if (card.rank === 'ACE') {
                aces++;
                value += 11;
            } else if (['JACK', 'QUEEN', 'KING'].includes(card.rank)) {
                value += 10;
            } else {
                value += parseInt(card.rank) || 0;
            }
        }
    });
    
    while (value > 21 && aces > 0) {
        value -= 10;
        aces--;
    }
    
    return value;
}

// Game actions
async function hit() {
    if (!currentGame) return;
    
    try {
        const game = await apiRequest(`/games/${currentGame.id}/hit`, {
            method: 'POST'
        });
        
        currentGame = game;
        updateGameDisplay();
        
        if (game.status === 'COMPLETED') {
            handleGameEnd();
        }
    } catch (error) {
        showMessage(`Error hitting: ${error.message}`, 'error');
    }
}

async function stand() {
    if (!currentGame) return;
    
    try {
        const game = await apiRequest(`/games/${currentGame.id}/stand`, {
            method: 'POST'
        });
        
        currentGame = game;
        updateGameDisplay();
        handleGameEnd();
    } catch (error) {
        showMessage(`Error standing: ${error.message}`, 'error');
    }
}

async function split() {
    if (!currentGame) return;
    
    try {
        const game = await apiRequest(`/games/${currentGame.id}/split`, {
            method: 'POST'
        });
        
        currentGame = game;
        updateGameDisplay();
        showMessage('Hand split successfully!', 'success');
    } catch (error) {
        showMessage(`Error splitting: ${error.message}`, 'error');
    }
}

async function insurance() {
    if (!currentGame) return;
    
    try {
        const game = await apiRequest(`/games/${currentGame.id}/insurance`, {
            method: 'POST'
        });
        
        currentGame = game;
        updateGameDisplay();
        showMessage('Insurance taken!', 'success');
    } catch (error) {
        showMessage(`Error taking insurance: ${error.message}`, 'error');
    }
}

function handleGameEnd() {
    let message = '';
    let statusClass = '';
    
    if (currentGame.result) {
        switch (currentGame.result) {
            case 'PLAYER_WIN':
                message = '🎉 You Win!';
                statusClass = 'win';
                break;
            case 'DEALER_WIN':
                message = '😞 Dealer Wins';
                statusClass = 'lose';
                break;
            case 'PUSH':
                message = '🤝 Push (Tie)';
                statusClass = 'push';
                break;
            case 'PLAYER_BLACKJACK':
                message = '🎉 Blackjack! You Win!';
                statusClass = 'win';
                break;
            case 'PLAYER_BUST':
                message = '💥 Bust! You Lose';
                statusClass = 'lose';
                break;
            case 'DEALER_BUST':
                message = '🎉 Dealer Bust! You Win!';
                statusClass = 'win';
                break;
        }
    }
    
    const gameStatus = document.getElementById('gameStatus');
    gameStatus.textContent = message;
    gameStatus.className = `game-status ${statusClass}`;
    gameStatus.style.display = 'block';
    
    setTimeout(() => {
        updatePlayerStats();
        resetForNewGame();
    }, 3000);
}

function resetForNewGame() {
    currentGame = null;
    document.getElementById('gameBoard').style.display = 'none';
    document.getElementById('betSection').style.display = 'block';
    document.getElementById('gameStatus').style.display = 'none';
    
    document.getElementById('playerHand').innerHTML = '';
    document.getElementById('dealerHand').innerHTML = '';
    document.getElementById('playerValue').textContent = '0';
    document.getElementById('dealerValue').textContent = '0';
} 