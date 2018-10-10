USE jship;

SELECT * FROM stats WHERE UNo = (SELECT UNo FROM users WHERE UName = '...');
