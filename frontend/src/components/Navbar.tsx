import React, { useState } from 'react';
import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import { Sun, Moon, Search, Bell, User as UserIcon } from 'lucide-react';
import api from '../api/client';

export const Navbar: React.FC = () => {
  const { theme, toggleTheme } = useTheme();
  const { user } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [showSearchModal, setShowSearchModal] = useState(false);

  const handleSearch = async (q: string) => {
    setSearchQuery(q);
    if (q.trim().length >= 2) {
      try {
        const res = await api.get(`/search?query=${encodeURIComponent(q)}`);
        setSearchResults(res.data);
        setShowSearchModal(true);
      } catch (err) {
        console.error(err);
      }
    } else {
      setSearchResults([]);
      setShowSearchModal(false);
    }
  };

  return (
    <header className="h-16 glass-nav sticky top-0 z-30 px-6 flex items-center justify-between">
      {/* Global Search Bar */}
      <div className="relative w-72 md:w-96">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => handleSearch(e.target.value)}
            placeholder="Search students, teachers, classes..."
            className="w-full pl-10 pr-4 py-2 rounded-xl bg-slate-100/70 dark:bg-slate-800/70 border border-slate-200/50 dark:border-slate-700/50 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50 transition-all"
          />
        </div>

        {/* Search Results Dropdown Modal */}
        {showSearchModal && searchResults.length > 0 && (
          <div className="absolute top-12 left-0 w-full glass-card p-2 z-50 max-h-80 overflow-y-auto space-y-1 shadow-2xl">
            {searchResults.map((item, idx) => (
              <a
                key={idx}
                href={item.url}
                onClick={() => setShowSearchModal(false)}
                className="flex items-center justify-between p-2.5 rounded-lg hover:bg-slate-200/50 dark:hover:bg-slate-800/50 transition-colors"
              >
                <div>
                  <p className="font-semibold text-sm">{item.title}</p>
                  <p className="text-xs text-slate-500">{item.subtitle}</p>
                </div>
                <span className="text-[10px] px-2 py-0.5 rounded-md bg-indigo-500/10 text-indigo-500 font-bold uppercase">
                  {item.category}
                </span>
              </a>
            ))}
          </div>
        )}
      </div>

      {/* Right Controls */}
      <div className="flex items-center space-x-4">
        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          className="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
          title="Toggle Theme"
        >
          {theme === 'dark' ? <Sun size={18} className="text-amber-400" /> : <Moon size={18} className="text-indigo-600" />}
        </button>

        {/* Notifications Icon */}
        <div className="relative">
          <button className="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors relative">
            <Bell size={18} />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-indigo-500 animate-ping" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-indigo-500" />
          </button>
        </div>

        {/* Profile */}
        <div className="flex items-center space-x-3 pl-3 border-l border-slate-200/50 dark:border-slate-800/50">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-500 to-violet-500 flex items-center justify-center text-white font-bold text-sm shadow-md">
            {user?.username?.charAt(0).toUpperCase()}
          </div>
          <div className="hidden md:block">
            <p className="text-sm font-semibold">{user?.username}</p>
            <p className="text-[11px] text-slate-500">{user?.email}</p>
          </div>
        </div>
      </div>
    </header>
  );
};
