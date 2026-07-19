import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { DollarSign, Receipt, Plus } from 'lucide-react';

export const FeesPage: React.FC = () => {
  const [payments, setPayments] = useState<any[]>([]);
  const [structures, setStructures] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFeesData();
  }, []);

  const fetchFeesData = async () => {
    try {
      const [resPay, resStruct] = await Promise.all([
        api.get('/fees/payments'),
        api.get('/fees/structure'),
      ]);
      setPayments(resPay.data);
      setStructures(resStruct.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Fees & Finance Portal</h1>
        <p className="text-sm text-slate-500">Track tuition structure, payments history, and digital receipts.</p>
      </div>

      {/* Fee Structures Grid */}
      <div className="glass-card p-6">
        <h2 className="text-lg font-bold mb-4">Academic Fee Structure</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {structures.map((s) => (
            <div key={s.id} className="p-4 rounded-xl bg-slate-800/40 border border-slate-700/50 space-y-2">
              <div className="flex justify-between items-center">
                <span className="font-bold">{s.className}</span>
                <span className="text-xs px-2 py-0.5 rounded bg-indigo-500/10 text-indigo-400 font-semibold">{s.feeType}</span>
              </div>
              <p className="text-2xl font-bold text-emerald-400">${s.amount}</p>
              <p className="text-xs text-slate-500">Due Date: {s.dueDate}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Payment Transactions List */}
      <div className="glass-card overflow-hidden p-6 space-y-4">
        <h2 className="text-lg font-bold">Recent Payment Collections</h2>
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-800 text-xs font-semibold text-slate-500 uppercase">
              <th className="p-3">Receipt No</th>
              <th className="p-3">Student Name</th>
              <th className="p-3">Fee Type</th>
              <th className="p-3">Amount Paid</th>
              <th className="p-3">Method</th>
              <th className="p-3">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800 text-sm">
            {payments.map((p) => (
              <tr key={p.id}>
                <td className="p-3 font-mono text-indigo-400 font-bold">{p.receiptNo}</td>
                <td className="p-3 font-semibold">{p.studentName}</td>
                <td className="p-3">{p.feeType}</td>
                <td className="p-3 font-bold text-emerald-400">${p.amountPaid}</td>
                <td className="p-3 uppercase text-xs font-mono">{p.paymentMethod}</td>
                <td className="p-3">
                  <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-500/10 text-emerald-400">
                    {p.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
