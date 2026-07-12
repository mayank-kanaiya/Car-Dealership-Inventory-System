import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Plus,
  ShoppingCart,
  PackagePlus,
  Trash2,
  Edit,
  Car,
  DollarSign,
  AlertTriangle,
  BarChart3,
} from 'lucide-react';
import {
  useVehicleList,
  useDeleteVehicle,
  usePurchase,
  useRestock,
} from '../features/vehicles/hooks/useVehicles';
import { PurchaseModal, RestockModal } from '../features/vehicles/components/InventoryModals';
import ConfirmDialog from '../components/ConfirmDialog/ConfirmDialog';
import Spinner from '../components/Spinner/Spinner';
import ErrorDisplay from '../components/ErrorDisplay/ErrorDisplay';
import Button from '../components/Button/Button';
import toast from 'react-hot-toast';

function StatCard({ icon: Icon, label, value, color, bgColor }) {
  return (
    <div className="bg-surface rounded-2xl border border-border p-5 sm:p-6">
      <div className="flex items-center gap-4">
        <div className={`flex items-center justify-center w-12 h-12 rounded-xl ${bgColor}`}>
          <Icon size={22} className={color} />
        </div>
        <div>
          <p className="text-sm text-text-secondary">{label}</p>
          <p className="text-2xl font-bold text-text-primary">{value}</p>
        </div>
      </div>
    </div>
  );
}

function AdminDashboardPage() {
  const navigate = useNavigate();
  const { data, isLoading, error } = useVehicleList({ page: 0, size: 50 });
  const deleteVehicle = useDeleteVehicle();
  const purchase = usePurchase();
  const restock = useRestock();

  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [modalType, setModalType] = useState(null);

  const vehicles = data?.content || [];

  const stats = useMemo(() => {
    const totalValue = vehicles.reduce((sum, v) => sum + v.price * v.quantityInStock, 0);
    const lowStock = vehicles.filter((v) => v.quantityInStock > 0 && v.quantityInStock < 5).length;
    const categories = new Set(vehicles.map((v) => v.category)).size;
    return { totalValue, lowStock, categories, total: vehicles.length };
  }, [vehicles]);

  const handleDelete = async () => {
    try {
      await deleteVehicle.mutateAsync(selectedVehicle.id);
      toast.success('Vehicle deleted');
    } catch (err) {
      toast.error(err.message || 'Failed to delete');
    }
    setModalType(null);
    setSelectedVehicle(null);
  };

  const handlePurchase = async (id, quantity) => {
    try {
      await purchase.mutateAsync({ id, quantity });
      toast.success('Purchase successful');
    } catch (err) {
      toast.error(err.message || 'Failed to purchase');
    }
    setModalType(null);
    setSelectedVehicle(null);
  };

  const handleRestock = async (id, quantity) => {
    try {
      await restock.mutateAsync({ id, quantity });
      toast.success('Restock successful');
    } catch (err) {
      toast.error(err.message || 'Failed to restock');
    }
    setModalType(null);
    setSelectedVehicle(null);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Spinner size="lg" />
      </div>
    );
  }

  if (error) {
    return <ErrorDisplay message={error.message} />;
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-primary/10">
            <BarChart3 size={20} className="text-primary" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-text-primary">Admin Dashboard</h1>
            <p className="text-sm text-text-secondary">Manage your vehicle inventory</p>
          </div>
        </div>
        <Button
          variant="primary"
          icon={<Plus size={16} />}
          onClick={() => navigate('/vehicles/new')}
        >
          Add Vehicle
        </Button>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard
          icon={Car}
          label="Total Vehicles"
          value={stats.total}
          color="text-primary"
          bgColor="bg-primary/10"
        />
        <StatCard
          icon={DollarSign}
          label="Inventory Value"
          value={`$${stats.totalValue.toLocaleString()}`}
          color="text-success"
          bgColor="bg-green-50 dark:bg-green-950/30"
        />
        <StatCard
          icon={AlertTriangle}
          label="Low Stock"
          value={stats.lowStock}
          color="text-warning"
          bgColor="bg-amber-50 dark:bg-amber-950/30"
        />
        <StatCard
          icon={BarChart3}
          label="Categories"
          value={stats.categories}
          color="text-primary"
          bgColor="bg-primary-light/50"
        />
      </div>

      <div className="bg-surface rounded-2xl border border-border overflow-hidden">
        <div className="px-6 py-4 border-b border-border">
          <h2 className="text-lg font-semibold text-text-primary">All Vehicles</h2>
        </div>
        <div className="divide-y divide-border">
          {vehicles.map((vehicle) => (
            <div
              key={vehicle.id}
              className="flex items-center gap-4 px-6 py-4 hover:bg-surface-hover transition-colors"
            >
              <div className="w-16 h-16 rounded-xl bg-surface-secondary overflow-hidden flex-shrink-0">
                <img
                  src={vehicle.imageUrl || '/images/default-vehicle.svg'}
                  alt={`${vehicle.make} ${vehicle.model}`}
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    e.target.src = '/images/default-vehicle.svg';
                  }}
                />
              </div>
              <div className="flex-1 min-w-0">
                <h3 className="text-sm font-semibold text-text-primary truncate">
                  {vehicle.make} {vehicle.model}
                </h3>
                <p className="text-xs text-text-secondary">{vehicle.category.replace('_', ' ')}</p>
              </div>
              <div className="hidden sm:block text-right">
                <p className="text-sm font-semibold text-text-primary">
                  ${vehicle.price.toLocaleString()}
                </p>
                <p
                  className={`text-xs font-medium ${
                    vehicle.quantityInStock > 0 ? 'text-success' : 'text-danger'
                  }`}
                >
                  {vehicle.quantityInStock > 0
                    ? `${vehicle.quantityInStock} in stock`
                    : 'Out of stock'}
                </p>
              </div>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => navigate(`/vehicles/${vehicle.id}/edit`)}
                  className="p-2 rounded-lg text-text-secondary hover:text-primary hover:bg-primary/10 transition-colors cursor-pointer"
                  aria-label="Edit"
                >
                  <Edit size={15} />
                </button>
                <button
                  onClick={() => {
                    setSelectedVehicle(vehicle);
                    setModalType('purchase');
                  }}
                  className="p-2 rounded-lg text-text-secondary hover:text-primary hover:bg-primary/10 transition-colors cursor-pointer"
                  aria-label="Sell"
                >
                  <ShoppingCart size={15} />
                </button>
                <button
                  onClick={() => {
                    setSelectedVehicle(vehicle);
                    setModalType('restock');
                  }}
                  className="p-2 rounded-lg text-text-secondary hover:text-success hover:bg-green-50 dark:hover:bg-green-950/30 transition-colors cursor-pointer"
                  aria-label="Restock"
                >
                  <PackagePlus size={15} />
                </button>
                <button
                  onClick={() => {
                    setSelectedVehicle(vehicle);
                    setModalType('delete');
                  }}
                  className="p-2 rounded-lg text-text-secondary hover:text-danger hover:bg-red-50 dark:hover:bg-red-950/30 transition-colors cursor-pointer"
                  aria-label="Delete"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {selectedVehicle && modalType === 'purchase' && (
        <PurchaseModal
          vehicle={selectedVehicle}
          isOpen
          onClose={() => {
            setModalType(null);
            setSelectedVehicle(null);
          }}
          onConfirm={handlePurchase}
          isSubmitting={purchase.isPending}
        />
      )}
      {selectedVehicle && modalType === 'restock' && (
        <RestockModal
          vehicle={selectedVehicle}
          isOpen
          onClose={() => {
            setModalType(null);
            setSelectedVehicle(null);
          }}
          onConfirm={handleRestock}
          isSubmitting={restock.isPending}
        />
      )}
      {selectedVehicle && modalType === 'delete' && (
        <ConfirmDialog
          isOpen
          title="Delete Vehicle"
          message={`Are you sure you want to delete ${selectedVehicle.make} ${selectedVehicle.model}? This action cannot be undone.`}
          onConfirm={handleDelete}
          onCancel={() => {
            setModalType(null);
            setSelectedVehicle(null);
          }}
        />
      )}
    </div>
  );
}

export default AdminDashboardPage;
