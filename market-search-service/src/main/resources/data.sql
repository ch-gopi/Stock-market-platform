INSERT INTO stock_meta (
    symbol, name, type, region, market_open, market_close, timezone, currency,
    match_score, price, change, change_percent, volume, historical_performance
) VALUES
    ('AAPL', 'Apple Inc', 'Equity', 'United States', '09:30', '16:00', 'UTC-04', 'USD',
     1.0, 189.5, -2.3, -1.2, 1200000, 185.0),
    ('MSFT', 'Microsoft Corp', 'Equity', 'United States', '09:30', '16:00', 'UTC-04', 'USD',
     0.98, 200.2, 2.0, 1.0, 1333330, 200.0),
    ('NVDA', 'NVIDIA Corp', 'Equity', 'United States', '09:30', '16:00', 'UTC-04', 'USD',
     0.95, 0, 0, 0, 0, 0);
