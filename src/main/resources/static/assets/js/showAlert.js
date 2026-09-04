let notyfInstance;

try {
    if (typeof Notyf !== 'undefined') {
        notyfInstance = new Notyf({
            position: {
                x: 'right',
                y: 'top'
            },
            types: [
                {
                    type: 'success',
                    className: 'text-white',
                    background: 'rgb(25,135,84)',
                    icon: {
                        className: 'fa-solid fa-circle-check text-white',
                        tagName: 'i',
                        text: '',
                    },
                },
                {
                    type: 'error',
                    className: 'text-white',
                    background: 'rgb(220,53,69)',
                    icon: {
                        className: 'fa-solid fa-circle-xmark text-white',
                        tagName: 'i',
                        text: '',
                    },
                },
                {
                    type: 'warning',
                    className: 'text-dark',
                    background: 'rgb(255,193,7)',
                    icon: {
                        className: 'fa-solid fa-triangle-exclamation text-dark',
                        tagName: 'i',
                        text: '',
                    },
                },
                {
                    type: 'info',
                    className: 'text-dark',
                    background: 'rgb(13,202,240)',
                    icon: {
                        className: 'fa-solid fa-circle-info text-dark',
                        tagName: 'i',
                        text: '',
                    },
                }
            ],
            dismissible: true
        });
    }
} catch (e) {
    console.error("Lỗi khởi tạo Notyf:", e);
}

function getNotyf() {
    if (!notyfInstance && typeof Notyf !== 'undefined') {
        notyfInstance = new Notyf({
            position: { x: 'right', y: 'top' },
            dismissible: true
        });
    }
    return notyfInstance;
}

const showAlert = (state, message, duration = 3500) => {
    if (!message) return;
    const instance = getNotyf();
    if (instance) {
        instance.open({
            type: state,
            message: message,
            duration: duration
        });
    } else {
        alert(message);
    }
};

window.showAlert = showAlert;

const confirmLogout = () => {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: 'Bạn có chắc muốn đăng xuất?',
            text: 'Phiên làm việc sẽ kết thúc',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Đăng xuất',
            cancelButtonText: 'Huỷ',
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6'
        }).then((result) => {
            if (result.isConfirmed) {
                showAlert('success', 'Đăng xuất thành công!');
                setTimeout(() => {
                    window.location.href = "logout";
                }, 2000);
            }
        });
    }
};

window.confirmLogout = confirmLogout;