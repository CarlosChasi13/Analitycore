import axios from 'axios';

export default axios.create({
  baseURL: 'https://analitycore-python-47k6.onrender.com/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000
});
